var googleMap = {
	_map : "map", // private 変数 mapを格納
	_marker : "marker", // private 変数 markerを格納
	_markertmp : "markertmp", // private 変数 markerを格納(最短距離を求める場合) 
	_presentLoc : "presentLoc", // private変数 現在位置を格納
	_route : "route", // private変数 ルートを格納
	_allLocation : "location", // private変数 customerVisitListTableの全てのLatLng情報を格納
	_optimizedResult : "optimizedResult",// private変数 得られた最適化データを格納
	_selectLocation : "selectLocation",// private変数 ユーザが選択した行を順番に格納
	/**
	 * 初期化します。 現在位置を表示します。
	 */
	initializeMap : function() {
		// 現在位置を表示
		var options = {
			enableHighAccuracy : true,
			maximumAge : 0
		};
		navigator.geolocation.getCurrentPosition(function success(pos) {
			// 現在位置を中心とした地図を表示
			var crd = pos.coords;
			googleMap._presentLoc = new google.maps.LatLng(crd.latitude,
					crd.longitude);
			var opts = {
				zoom : 15,
				center : googleMap._presentLoc,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			};
			googleMap._map = new google.maps.Map(document
					.getElementById("mapList"), opts);
			// ルート最適化に利用する
			rendererOptions = {
				draggable : false,
				preserveViewport : false,
				suppressMarkers : true,
				strokeColor: '#FF0000',
		        strokeWeight: 4,
		        strokeOpacity: 0.7
			};
			googleMap._route = new google.maps.DirectionsRenderer(
					rendererOptions);
			googleMap._route.setMap(googleMap._map);

			googleMap._marker = new Object();
			googleMap._markertmp = new Object();
			googleMap._allLocation = [];
			googleMap.displayMarker(googleMap._presentLoc, 0,0,'new');
			googleMap._selectLocation=[];//ユーザが選択した数を初期値0とする。
			googleMap._selectLocation.push(0);
			//loadingを終了します。
			index.endLoading();
		}, function error(err) {
			// TODO エラーの場合どうするか
		}, options);
	},
	/**
	 * 地図上に指定したマーカーを表示します。
	 * 
	 * @param address
	 *            住所
	 */
	addMarker : function(address, index) {
		var geocoder = new google.maps.Geocoder();
		var goalLoc;
		geocoder.geocode({
			'address' : address,
			'language' : 'ja',
			'region' : 'jp'
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var bounds = new google.maps.LatLngBounds();
				for ( var r in results) {
					if (results[r].geometry) {
						var goalLoc = results[r].geometry.location;
//						googleMap._selectLocation++;
						googleMap.displayMarker(goalLoc, index,googleMap._selectLocation.length,'new');
						googleMap._selectLocation.push(index);
						bounds.extend(goalLoc);
					}
				}
			} else {
				// TODO これでよいか
				alert("Geocode 取得に失敗しました reason: " + status);
			}
		});
	},
	updateMarker : function(address,index,max,number) {
		var geocoder = new google.maps.Geocoder();
		var goalLoc;
		geocoder.geocode({
			'address' : address[number],
			'language' : 'ja',
			'region' : 'jp'
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var bounds = new google.maps.LatLngBounds();
				for ( var r in results) {
					if (results[r].geometry) {
						var goalLoc = results[r].geometry.location;
						googleMap.displayMarker(goalLoc, index[number],number+1,'new');
						googleMap._selectLocation.push(index[number]);
						bounds.extend(goalLoc);
					}
				}
			} else {
				// TODO これでよいか
				alert("Geocode 取得に失敗しました reason: " + status);
			}
			if(number+1 < max -1){
				googleMap.updateMarker(address,index,max,number+1);
			}
		});
	},
	/**
	 * customervisitListTableの全てのお客様のLatLng情報を取得します。
	 * @param addressList 住所リスト
	 */
	addLocation : function(addressList){
		var geocoder = new google.maps.Geocoder();
		geocoder.geocode({
			'address' : addressList.shift(),
			'language' : 'ja',
			'region' : 'jp'
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				for ( var r in results) {
					if (results[r].geometry) {
						googleMap._allLocation.push(results[r].geometry.location);
					}
				}
			} else {
				// TODO これでよいか
				alert("Geocode 取得に失敗しました reason: " + status);
			}
			if (addressList.length) {
				googleMap.addLocation(addressList);
			} else{
				//全てのデータ取得終わったら
				var distance = [];
				var goal = googleMap._allLocation;
				var loc = [];
				googleMap.calculateNearDistance(goal,loc,distance, 9999999999999999999999999999);
			}
		});
	},
	/**
	 * 地図上に指定したマーカーを削除します。
	 * @param index 選択した行番号
	 * @param customerAddresses　行番号に該当する住所のオブジェクト
	 */
	deleteMarker : function(index,customerAddresses) {
		//選んだ順番にテーブルの行を格納した配列
		var tmpindex=[];
		//選んだ順番を考慮した住所の配列
		var localcustomerAddresses=[];
		//選んだお客様の数
		var selectNumber;
		
		var marker = this._marker[index];
		marker.setMap(null);
		delete this._marker[index];

		//削除したマーカーを_selectLocationから削除
		for(var i = 0 ;i<googleMap._selectLocation.length;i++){
			if(googleMap._selectLocation[i]==index){
				googleMap._selectLocation.splice(i, 1);
			}
		}
		//最後のチェックボックスのチェックをはずす場合は行わない。
		if(Object.keys(customerAddresses).length > 0){

		//マーカーを入れ替え 一旦削除
		for(var i = 1 ;i<googleMap._selectLocation.length;i++){
			tmpindex.push(googleMap._selectLocation[i]);
			var tmpmarker = this._marker[googleMap._selectLocation[i]];
			tmpmarker.setMap(null);
			delete this._marker[googleMap._selectLocation[i]];
		}
		selectNumber=googleMap._selectLocation.length;
		googleMap._selectLocation.splice(1, selectNumber-1);
		//マーカー入れ替え　画像差し替え
		for(var i=0;i<tmpindex.length;i++){
			localcustomerAddresses.push(customerAddresses[tmpindex[i]]);
		}


			googleMap.updateMarker(localcustomerAddresses,tmpindex,selectNumber,0);

		}
	},
	/**
	 * マーカーを表示します。
	 * @param presentLoc 現在地点
	 * @param index customervisitListTableの行番号
	 * @param m 最適化後の番号
	 * @param condition 新規作成new 更新update
	 */
	displayMarker : function(presentLoc,index,m,condition) {
		var marker;
		if(index == 0){
			marker = new google.maps.Marker({
				position : presentLoc,
				map : this._map,
				icon: 'image/currentLocation.png'
			});
			this._marker[index] = marker;
		} else{
			var img = new google.maps.MarkerImage(
					'image/opt'+m+'.png',
					new google.maps.Size(31,31),
					new google.maps.Point(0,0),
					new google.maps.Point(16,16)
			);
			if(condition=='new'){
				marker = new google.maps.Marker({
					position : presentLoc,
					map : this._map
				});
				marker.setIcon(img);
				this._marker[index] = marker;
			}else{
				this._marker[index].setIcon(img);
				marker = this._marker[index];
			}
			//範囲を設定
			var bounds = googleMap.adjustZoom(this._marker);
			//マーカーが全て収まるように地図の中心とズームを調整して表示
			this._map.fitBounds(bounds);
		}
		googleMap.attachMessage(marker, index);
	},
	/**
	 * 最短距離のみのマーカーを表示します。
	 * @param presentLoc 現在地点
	 * @param index customervisitListTableの行番号
	 * @param m 順位　これでマーカーの種類を識別する
	 */
	displayMarkerTmp : function(presentLoc,index,m) {
		var marker = new google.maps.Marker({
			position : presentLoc,
			flat:false,
			map : this._map,
			icon: 'image/'+m+'.png'
		});
		this._markertmp[index] = marker;
		//範囲を設定
		var bounds = googleMap.adjustZoom(this._markertmp);
		//マーカーが全て収まるように地図の中心とズームを調整して表示
		this._map.fitBounds(bounds);
		//表示項目を作成します
		googleMap.attachMessage(marker,index);
	},
	tmpGoCustomer : function(){
		alert('お客様詳細情報へ');
	},
	/**
	 * マーカーに文字を表示させます。
	 * 
	 * @param marker
	 *            マーカー
	 * @param msg
	 *            メッセージ
	 */
	attachMessage : function(marker,index) {
		var msg;
		if(index == 0){
			msg = '現在地'
		} else{
			var row = jQuery('#customerListTable tr').eq(index);
			//お客様住所
			var customerAddrress = jQuery(row).find('td.customerAddress').text();
			//お客様名
			var customerName = jQuery(row).find('td.customerName').text();
			msg = '<a href="JavaScript:googleMap.tmpGoCustomer()；">'+customerName+'</a>'+ '<br>' + customerAddrress;
		}
		google.maps.event.addListener(marker, 'click', function(event) {
			new google.maps.InfoWindow({
				content : msg
			}).open(marker.getMap(), marker);
		});
	},
	/**
	 * 登録したマーカーと現在地点をもとに地図のズームを最適化します。
	 * @param marker 登録されているマーカー
	 * @returns {google.maps.LatLngBounds}
	 */
	adjustZoom : function(marker){
		//現在地と追加したマーカーの位置でズームを修正する。
		//緯度経度の最大値(現在地)、最小値の初期値
		var minLat = googleMap._presentLoc.lat();
		var maxLat = 0;
		var minLng = googleMap._presentLoc.lng();
		var maxLng = 0;
		if (marker != null) {
			for (key in marker) {
				var Lat = marker[key].position.lat();
				var Lng = marker[key].position.lng();
				//中心座標を取得するため、緯度経度の最小値と最大値を取得する
				if(Lat < minLat){ minLat = Lat; }
				if(Lat > maxLat){ maxLat = Lat; }
				if(Lng < minLng){ minLng = Lng; }
				if(Lng > maxLng){ maxLng = Lng; }
			}
		}
		//北西端の座標を設定
		var sw = new google.maps.LatLng(maxLat,minLng);
		//東南端の座標を設定
		var ne = new google.maps.LatLng(minLat,maxLng);
		//範囲を設定
		return bounds = new google.maps.LatLngBounds(sw, ne);
	},
	/**
	 * ユーザが選択した訪問先の中で一番遠いところを最終目的地として経路を最適化します。
	 */
	calculateMaxDistance : function() {
		var distance = {};
		var listMarker = [];
		var listKey = [];
		for (key in this._marker) {
			if (key != 0) {
				listMarker.push(this._marker[key].position);
				listKey.push(key);
			}
		}
		// vivistListTableを並び替えするために全てのListを削除します。
		visitList.deleteAllVisitList();

		googleMap.calculateDistance(this._presentLoc, listMarker, distance,
				listKey, 0, 'max');
	},
	/**
	 * ユーザが選択した訪問先の中で一番近いところを最終目的地として経路を最適化します。
	 */
	calculateMinDistance : function() {

		var distance = {};
		var listMarker = [];
		var listKey = [];
		for (key in this._marker) {
			if (key != 0) {
				listMarker.push(this._marker[key].position);
				listKey.push(key);
			}
		}
		// vivistListTableを並び替えするために全てのListを削除します。
		visitList.deleteAllVisitList();
		
		googleMap.calculateDistance(this._presentLoc, listMarker, distance,
				listKey, 9999999999999999999999999999, 'min');
//		jQuery('.schedueButton').attr('onclick', "JavaScript:visitList.addEvents();");
	},
	/**
	 * 現在地から一番近いお客様を検索します。
	 */
	searchNearCustomer : function(){
		var len = $("#customerListTable tbody").children().length;
		var addressList = [];
		for(var i = 1; i< len;i++){
			var row = jQuery('#customerListTable tr').eq(i);
			//お客様住所
			var customerAddress = jQuery(row).find('td.customerAddress').text();
			addressList.push(customerAddress);
		}		
		index.startLoading();
		googleMap.addLocation(addressList);
	},
	/**
	 * 最適距離を計算してテーブルを動的に変化させマップにルートを表示させます。
	 * @param present 現在地点
	 * @param listMarker ユーザが訪問したい場所
	 * @param distance 現在地点から各地点の最適ルート距離
	 * @param listKey customerVisitListの行番号
	 * @param _d 最終地点を決めるための距離の初期値
	 * @param condition 目的地の距離状況(max or min)
	 */
	calculateDistance : function(present, listMarker, distance, listKey, _d,
			condition) {
		var directionsService = new google.maps.DirectionsService();
		var request = {
			origin : present,
			destination : listMarker.shift(),
			travelMode : google.maps.DirectionsTravelMode.DRIVING,
			unitSystem : google.maps.DirectionsUnitSystem.METRIC,
			optimizeWaypoints : true,
			avoidHighways : true,
			avoidTolls : true
		};

		// Googleルート検索機能を呼び出します。
		directionsService
				.route(
						request,
						function(response, status) {
							if (status == google.maps.DirectionsStatus.OK) {
								var route = response.routes[0];
								distance[listKey.shift()] = route.legs[0].distance.value;
								if (listMarker.length) {
									googleMap.calculateDistance(present,
											listMarker, distance, listKey, _d,
											condition);
								} else {
									var d = _d;// 最長距離
									var j = 0;// 最長距離のマーカーのcustomerVistTableの行番号
									// 現在位置から最長距離が分かる
									if (condition == 'max') {
										for (l in distance) {
											if (d < distance[l]) {
												d = distance[l];
												j = l;
											}
										}
									} else {
										for (l in distance) {
											if (d > distance[l]) {
												d = distance[l];
												j = l;
											}
										}
									}
									// ルート最適化
									var directionsService = new google.maps.DirectionsService();
									myWaypoints = [];
									var routeNum = {};// myWaypointsに格納された順番とマーカーキーの対応付け
									var num = 0;// myWaypointsに格納された順番
									for (key in googleMap._marker) {
										if (key != 0 && key != j) {
											myWaypoints
													.push({
														location : googleMap._marker[key].position,
														stopover : true
													});
											routeNum[num] = key;
											num++;
										}
									}

									var request = {
										origin : googleMap._presentLoc,
										destination : googleMap._marker[j].position,
										travelMode : google.maps.DirectionsTravelMode.DRIVING,
										unitSystem : google.maps.DirectionsUnitSystem.METRIC,
										waypoints : myWaypoints,
										optimizeWaypoints : true,
										avoidHighways : true,
										avoidTolls : true
									};
									directionsService
											.route(
													request,
													function(response, status) {
														if (status == google.maps.DirectionsStatus.OK) {
															googleMap._route
																	.setDirections(response);
															googleMap._optimizedResult = response.routes[0];
															var optimizedMyWaypoints = response.routes[0].waypoint_order;
															var optimizedRouteNum = {};// 最適化された順番とマーカーキーを紐付けした配列
															for (var i = 0; i < optimizedMyWaypoints.length; i++) {
																optimizedRouteNum[i] = routeNum[optimizedMyWaypoints[i]];
															}
															optimizedRouteNum[optimizedMyWaypoints.length] = j;
															// テーブルを順番に並び替えます&マーカーを作成する。
															for (key in optimizedRouteNum) {
																//テーブル更新
																visitList
																		.changevisitList(optimizedRouteNum[key]);
																//マーカー作成
																googleMap.displayMarker(googleMap._marker[optimizedRouteNum[key]].position,optimizedRouteNum[key],Number(key)+1,'update');
															}
															$("#buttonAlt").css("display", "none");
															$("#scheduleButon").css("display", "block");
														}
													});
								}
							}
						});
	},
	/**
	 * 最も近い場所を検索します。
	 * @param goal 目的地
	 * @param loc customerListTableにある全てのLatLng情報を格納 
	 * @param distance 距離の配列
	 * @param _d 近い場所を計算するために使います
	 * @param table customerListTable
	 */
	calculateNearDistance : function(goal,loc,distance, _d) {
		var directionsService = new google.maps.DirectionsService();
		var tmp = goal.shift();
		var request = {
			origin : this._presentLoc,
			destination : tmp,
			travelMode : google.maps.DirectionsTravelMode.DRIVING,
			unitSystem : google.maps.DirectionsUnitSystem.METRIC,
			optimizeWaypoints : true,
			avoidHighways : true,
			avoidTolls : true
		};

		// Googleルート検索機能を呼び出します。
		directionsService
				.route(request,function(response, status) {
							if (status == google.maps.DirectionsStatus.OK) {
								var route = response.routes[0];
								distance.push(route.legs[0].distance.value);
								loc.push(tmp);
								if (goal.length) {
									googleMap.calculateNearDistance(goal,loc, distance, _d);
								} else {
									var d = _d;// 距離
									var j = 0;// 距離に応じたマーカーのcustomerVistTableの行番号
									// 現在位置から距離が分かる
									var top3List = new Object();
									for(var k=1;k<=3;k++){
										for (var i = 0 ; i<distance.length;i++) {
											if (d > distance[i]) {
												d = distance[i];
												j = i+1;
											}
										}
										top3List[k]=j;
										//最短の値は削除する。要素数は変化なし
										delete distance[j-1];
										//初期化しなおし
										d=_d;
										j=0;
									}
									for(var m=1;m<=3;m++){
										var indexNum = top3List[m];
										var row = jQuery('#customerListTable tr').eq(indexNum);
										//お客様番号
										var customerId = jQuery(row).find('td.customerId').text();
										//お客様名
										var customerName = jQuery(row).find('td.customerName').text();
										
										googleMap.displayMarkerTmp(loc[indexNum-1],indexNum,m);
										visitList.addvisit(customerId,customerName,indexNum);
									}

									//後処理
									var len = jQuery("#customerListTable tbody").children().length;
									for(var i = 0;i<len;i++){
										delete googleMap._allLocation[i];
									}
									index.endLoading();
								}
							}
						});
	},
	/**
	 * 近場の訪問先をスケジュールに登録します。
	 * @param id お客様ID
	 * @param time 会議時間
	 * @param rowNum customervisiListTableに登録されている行番号
	 */
	getNearLatLng : function(id,time,rowNum){
		var directionsService = new google.maps.DirectionsService();
		var request = {
				origin : this._presentLoc,
				destination : this._markertmp[rowNum].position,
				travelMode : google.maps.DirectionsTravelMode.DRIVING,
				unitSystem : google.maps.DirectionsUnitSystem.METRIC,
				optimizeWaypoints : true,
				avoidHighways : true,
				avoidTolls : true
			};
		optimizedResult = new Object();
		directionsService.route(request, function(response, status) {
			if (status == google.maps.DirectionsStatus.OK) {
				var route = response.routes[0];
				optimizedResult['id'] = id;
				optimizedResult['time'] = time;	
				optimizedResult['duration'] = route.legs[0].duration.value;	
				//最適化結果を非同期通信で加工します。
				index.loadMain(optimizedResult);
			}
		});
	},
	/**
	 * 最適化された経路結果を返します。
	 * @returns {String}
	 */
	getOptimizedResult : function(){
		return this._optimizedResult;
	}
}