var index = {
	loadlogin : function() {
		$('#main').empty();
		$('#visit').empty();
		$('#info').empty();
		$('#login').load('./login.html', function() {
			//ログイン画面をロード 特に何もしない
		});
	},
	loadInfo : function() {
		$('#main').empty();
		$('#visit').empty();
		$('#login').empty();
		$('#info').load('./info.html', function() {
			//TODO 非同期通信実施
		});
	},
	/**
	 * ひとつのイベントをスケジュール登録します。
	 * @param optimizedResult
	 */
	loadMain : function(optimizedResult){
		$('#visit').empty();
		$('#info').empty();
		$('#login').empty();
		index.startLoading();
		$('#main').load('./main.html', function() {
			$.ajax({
			type : "post",
			url : "Customervist/schedule/initevent",
			data : optimizedResult,
			success : function() {
				schedule.displaySchedule();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert('登録失敗');
			},
			complete : function(data) {
				index.endLoading();
			}
		});
		});
	},
	/**
	 * 複数のイベントをスケジュール登録します。
	 * @param optimizedResult
	 */
	loadMains : function(optimizedResult){
		$('#visit').empty();
		$('#info').empty();
		$('#login').empty();
		$('#main').load('./main.html', function() {
			$.ajax({
			type : "post",
			url : "Customervist/schedule/initevents",
			traditional : true,
			data : optimizedResult,
			success : function() {
				schedule.displaySchedule();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert('登録失敗');
			},
			complete : function(data) {
				index.endLoading();
			}
		});
		});
	},
	/**
	 * メニュー画面からスケジュール機能を呼び出します。
	 */
	loadMainBack : function(){
		$('#visit').empty(); //テストのときはコメントアウト
		$('#info').empty();
		$('#login').empty();
		$('#main').load('./main.html', function() {
			schedule.displaySchedule();
		});
	},
	/**
	 * ログイン成功時に呼び出されます。
	 */
	loadVisit : function(){
		
		var user = $("#login_form").serialize();
		// loadingを開始します
		index.startLoading();
		$.ajax({
			type : "post",
			url : "Customervist/",
			data : user,
			success : function(msg, textStatus, xhr) {
				$('#menu').load('./menu.html', function() {});
				index.loadVisitList();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				jQuery("#user_id").val("");
				jQuery("#user_pwd").val("");
			},
			complete : function(data) {
				// loadingを終了します。
				index.endLoading();
			}
		});
	},
	/**
	 * メニュー画面から最適検索機能を呼び出します。
	 */
	loadVisitList : function(){
		$('#main').empty();
		$('#info').empty();
		$('#login').empty();
//		index.loadMainBack(); //テスト
		$('#visit').load('./visitList.html', function() {
			// loadingを開始します
			index.startLoading();
			$.ajax({
				type : "get",
				url : "Customervist/customer/list",
				success : function(list){
					//動的にテーブルを作成します。
					visitList.initializeCustomerListTable(list);
					//ルート検索エリア表示
					$('.fieldSearch').css('display', "block");
					//GoogleMap初期化
					googleMap.initializeMap();
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					//TODO JAX-RSリダイレクトできるか
					if (XMLHttpRequest.status == 401) {
						window.location = "./index.html"
					}
				},
				complete : function(data) {
				}
			});
		});
	},
	/**
	 * loadingを開始します
	 */
	startLoading : function() {
		$(".loading").html("<img src='image/gif-load.gif'/>");
		$('.loading').css({
			'background-size' : 'cover',
			'position' : 'absolute',
			'top' : '50%',
			'left' : '50%',
			'z-index': '6'
		});

	},
	/**
	 * loadingを終了します。
	 */
	endLoading : function() {
		$(".loading").empty();
		$('.loading').css({
			'background-size' : '',
			'position' : '',
			'top' : '',
			'left' : '',
			'z-index': ''
		});
	},
	/**
	 * ログアウトします。
	 */
	logout : function() {
//		window.location = "./index.html"
		$.ajax({
			type : "get",
			url : "Customervist/",
			success : function(msg) {
				//TODO
				window.location = "./index.html"
			}
		});
	},
	/**
	 * ログインキャンセルする場合に使用します、但しFireFoxではタブは消えません。画面が白くなります。
	 */
	cancel : function() {
		// キャンセルボタン押下されたらタブを消します
		window.open('about:blank', '_self').close();
	},
	displayRoute : function() {

		// 現在位置を表示
		var options = {
			enableHighAccuracy : true,
			maximumAge : 0
		};
		navigator.geolocation.getCurrentPosition(success, error, options);
	}
};
/**
 * 現在位置表示に成功した場合のコールバック関数
 * 
 * @param pos
 */
function success(pos) {
	rendererOptions = {
		draggable : true,
		preserveViewport : false
	};
	var directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);

	// 現在位置を中心とした地図を表示
	var crd = pos.coords;
	var presentLoc = new google.maps.LatLng(crd.latitude, crd.longitude);
	var opts = {
		zoom : 10,
		center : presentLoc,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"), opts);
	//マーカー表示
	googleMap.displayMarker(map,presentLoc,'hogehoge');
	directionsDisplay.setMap(map);
	google.maps.event.addListener(directionsDisplay, 'directions_changed',
			function() {
			});

	// 目的地の場所を表示
	var geocoder = new google.maps.Geocoder();
	var goalLoc;
	// TODO DBからデータ取得するようにする
	geocoder.geocode({
		'address' : '東京都足立区千住河原町27-1',
		'language' : 'ja',
		'region' : 'jp'
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var bounds = new google.maps.LatLngBounds();
			for ( var r in results) {
				if (results[r].geometry) {
					var goalLoc = results[r].geometry.location;
					bounds.extend(goalLoc);
					new google.maps.Marker({
						position : goalLoc,
						map : map
					});
				}
			}
			var directionsService = new google.maps.DirectionsService();
			// 複数の地点の最適化
			myWaypoints = [];
			myWaypoints.push({
				location : '東京都足立区千住2-63',
				stopover : true
			});
			myWaypoints.push({
				location : '東京都豊島区池袋本町4-46-11',
				stopover : true
			});
			myWaypoints.push({
				location : '東京都荒川区南千住4',
				stopover : true
			});
			var request = {
				origin : presentLoc,
				destination : goalLoc,
				travelMode : google.maps.DirectionsTravelMode.DRIVING,
				unitSystem : google.maps.DirectionsUnitSystem.METRIC,
				waypoints: myWaypoints,
				optimizeWaypoints : true,
				avoidHighways : true,
				avoidTolls : true
			};
			
			optimizedResult = new Object();
			directionsService.route(request, function(response, status) {
				if (status == google.maps.DirectionsStatus.OK) {
					directionsDisplay.setDirections(response);
					var route = response.routes[0];
					for (var i = 0; i < route.legs.length; i++) {
						optimizedResult['location'+i] = route.legs[i].end_address;
						optimizedResult['time'+i] = route.legs[i].duration.value;					

						console.log('スタート：'+route.legs[i].start_address);
						console.log('距離：'+route.legs[i].distance.value);
						console.log('時間：'+route.legs[i].duration.value);
						console.log('ゴール：'+route.legs[i].end_address);
					}
					//最適化結果を非同期通信で加工します。
					console.log(optimizedResult);
					$.ajax({
						type : "post",
						url : "Customervist/schedule/init",
						data : optimizedResult,
						success : function(eventSources) {
							schedule.displaySchedule(eventSources);
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							alert('失敗');
						},
						complete : function(data) {
						}
					});
				}
			});
		} else {
			alert("Geocode 取得に失敗しました reason: " + status);
		}
	});

};
/**
 * 現在位置表示に失敗した場合のコールバック関数
 * 
 * @param err
 */
function error(err) {
	alert("失敗");
}

jQuery(document).ready(function() {
	index.loadlogin();
});