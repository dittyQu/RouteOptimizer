var visitList = {

	drag : function(event){
		//お客様ID
		var row = jQuery(event.currentTarget).closest('tr');
		//お客様番号
		var customerId = jQuery(row).find('td.customerId').text();
		
		console.log(event.currentTarget.id);
		
//		jQuery(row).data('event',{
//			title:customerId +'先へ訪問',
//			stick:true
//		});
//		jQuery(row).draggable({
//			zIndex: 999,
//			revert: true,      // will cause the event to go back to its
//			revertDuration: 0  //  original position after the drag
//		});

		
		event.dataTransfer.effectAllowed = 'move';
		event.dataTransfer.setData("text", event.currentTarget.id);
//		event.dataTransfer.setData("row", row);
		
//		$('.customerTableRow').each(function() {

			// store data so the calendar knows to render an event upon drop
//			$(event.currentTarget).data('event', {
//				title: 'hoge', // use the element's text as the event title
//				stick: true // maintain when user navigates (see docs on the renderEvent method)
//			});
//
//			// make the event draggable using jQuery UI
//			$(event.currentTarget).draggable({
//				zIndex: 999,
//				revert: true,      // will cause the event to go back to its
//				revertDuration: 0  //  original position after the drag
//			});
//		});
	},
	dragover : function(event){
		  //dragoverイベントをキャンセルして、ドロップ先の要素がドロップを受け付けるようにする
		  event.preventDefault();
	},
	drop : function(event){
		//ドラッグされたデータのid名をDataTransferオブジェクトから取得
//		var row = event.dataTransfer.getData("row");
//		var customerId = jQuery(row).find('td.customerId').text();
//		console.log(customerId);
		
		  //ドラッグされたデータのid名をDataTransferオブジェクトから取得
		  var id_name = event.dataTransfer.getData("text");
		  //id名からドラッグされた要素を取得
		  var drag_elm =document.getElementById(id_name);
		//お客様番号
		var customerId = jQuery(drag_elm).find('td.customerId').text();
		var customerName = jQuery(drag_elm).find('td.customerName').text();
		  
		console.log('終了 : '+customerId);
		
		var element = document.createElement('div');
		$(element).addClass('selectCustomer');
		var date = common.formatDate(new Date());
		element.innerHTML = customerName+'先へ訪問';
		event.currentTarget.appendChild(element);
		  
		//ドロップ先にドラッグされた要素を追加
//		event.currentTarget.appendChild(drag_elm);
		//エラー回避のため、ドロップ処理の最後にdropイベントをキャンセルしておく
		event.preventDefault();
		
		$('#dropbox .selectCustomer').each(function() {
			// create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
			// it doesn't need to have a start or end
			var eventObject = {
					title : $.trim($(this).text()),
					id : 'meeting'+customerId + date
			// use the element's text as the event title
			};
			// store the Event Object in the DOM element so we can get to it later
			$(this).data('eventObject', eventObject);
			// make the event draggable using jQuery UI
			$(this).draggable({
				zIndex : 999,
				revert : true, // will cause the event to go back to its
				revertDuration : 0
			// original position after the drag
			});
		});
	},
	/**
	 * CustomerListTableを作成します。
	 * 
	 * @param list
	 */
	initializeCustomerListTable : function(list) {
		// Headerを付加
		$("#customerListTable").append(
				$('<tr>').append("<th></th>").append("<th>お客様ID</th>").append(
						"<th>お客様名</th>").append("<th>お客様住所</th>").append("<th>イベント情報</th>"));
		// 取得したデータを行に入れる
		for (var i = 0; i < list.length; i++) {
			$("#customerListTable")
				.append(
					$('<tr draggable="true" ondragstart="visitList.drag(event)" id=row'+i+'>')
				.append(
					'<td><input type="checkbox" name="check"/></td>')
				.append(
					$('<td class="customerId">').text(list[i]['customerId']))
				.append(
					$('<td class="customerName">').text(list[i]['customerName']))
				.append(
					$('<td class="customerAddress">').text(list[i]['customerAddress']))
				.append(
					$('<td class="customerEvent">').text(list[i]['event'])));
		}
		$('.stripe tr:odd').addClass('odd');
		
		
		// store data so the calendar knows to render an event upon drop
		$('.customerTableRow').data('event', {
			title: 'hoge', // use the element's text as the event title
			stick: true // maintain when user navigates (see docs on the renderEvent method)
		});

		// make the event draggable using jQuery UI
		$('.customerTableRow').draggable({
			zIndex: 999,
			revert: true,      // will cause the event to go back to its
			revertDuration: 0  //  original position after the drag
		});
		
		//チェックボックスイベント
		$('#customerListTable :checkbox').click(function(event){
			//行
			var row = jQuery(event.target).closest('tr');
			//行番号
			var roclaindex = jQuery(row).index();
			//お客様番号
			var customerId = jQuery(row).find('td.customerId').text();
			//お客様名
			var customerName = jQuery(row).find('td.customerName').text();
			//お客様住所
			var customerAddress = jQuery(row).find('td.customerAddress').text();

			if(jQuery(event.currentTarget).prop('checked')){
				//チェックされたとき
				//地図にマーカー表示
				googleMap.addMarker(customerAddress, roclaindex);
				//選択項目をテーブルに抽出
				visitList.addvisitList(customerId,customerName,roclaindex);
				
			} else{
				//チェック外されたとき
				//チェックがついている行のアドレスを取得
				var customerAddresses = new Object();
				jQuery('#customerListTable :checked').closest('tr').each(function(i,e){
					customerAddresses[$(e).index()] = $(e).find('td.customerAddress').text();
				});
				//地図上のマーカーを削除
				index.startLoading();
				googleMap.deleteMarker(roclaindex,customerAddresses);
				//選択項目をテーブルから削除
				visitList.deletevisitList(customerId);
				index.endLoading();
			}
			$("#buttonAlt").css("display", "block");
			$("#scheduleButon").css("display", "none");
		});
	},
	/**
	 * visitListTableに選択したお客様情報を追加します。
	 * 
	 * @param id
	 *            お客様ID
	 * @param name
	 *            お客様名
	 * @param index CustomerListTableの該当する行番号
	 */
	addvisitList : function(id, name,indexNum) {
		//visitListTableの最終行番号を取得
		var len = $("#visitListTable tbody").children().length;
		$("#visitListTable")
		.append(
			$('<tr>')
		.append(
			$('<td>').text(len+1))
		.append(
			$('<td class="customerId">>').text(id))
		.append(
			$('<td class="customerName">').text(name))
		.append(
			'<td><input type="text" class="eventTime" size="5" value="120"/>分</td>')
		.append(
			'<td><input type="hidden" name="near" class="near" value="'+indexNum+'"/></td>'));
		
		$('.stripe2 tr:odd').addClass('odd');
		$('.eventTime').spinner({
		    max: 240,
		    min: 30,
		    step: 10
		  });
		
		//スケジュールボタンを押下されたときの処理
		//TODO eventの取り扱い方
//		jQuery('#registerEvents').click(function(event){
//			index.startLoading();
//			var rowLen = $("#visitListTable tbody").children().length;
//			var optimizedRoute = googleMap.getOptimizedResult();
//			optimizedResult = new Object();
//			id = [];
//			time = [];
//			duration = [];
//			for(var i = 1; i<= rowLen;i++){
//				var row = jQuery('#visitListTable tr').eq(i);
//				//お客様ID
//				var customerId = jQuery(row).find('td.customerId').text();
//				//滞在時間
//				var eventTime = jQuery(row).find('input.eventTime').val();
//				id.push(customerId);
//				time.push(eventTime);
//				duration.push(optimizedRoute.legs[i-1].duration.value);
//			}
//			optimizedResult['id']=id;
//			optimizedResult['time']=time;
//			optimizedResult['duration']=duration;
//			index.loadMains(optimizedResult);
//			
//			return false;
//		});
	},
	/**
	 * visitTableに最も近いお客様情報を追加します。
	 * 
	 * @param id
	 *            お客様ID
	 * @param name
	 *            お客様名
	 * @param index CustomerListTableの該当する行番号
	 */
	addvisit : function(id, name, indexNum) {
		var len = $("#visitTable tbody").children().length;
		$("#visitTable")
		.append(
			$('<tr>')
		.append(
			$('<td>').text(len+1))
		.append(
			$('<td class="customerId">').text(id))
		.append(
			$('<td class="customerName">').text(name))
		.append(
			'<td><input type="text" class="eventTime" size="5" value="120"/>分</td>')
		.append(
			'<td><input type="button" onclick="JavaScript:visitList.registerEvent(this.parentNode.parentNode.rowIndex);" value="スケジュールに反映"></td>')
		.append(
			'<td><input type="hidden" name="near" class="near" value="'+indexNum+'"/></td>'));
		
		$('.stripe3 tr:odd').addClass('odd');
		// 一度最短検索したら再検索させない
		jQuery('#nearCustomer').attr('onclick', "javascript:void(0)");
		$('.eventTime').spinner({
		    max: 240,
		    min: 30,
		    step: 10
		  });
		
		//スケジュールボタンを押下されたときの処理
		//TODO event制御どうするか調査
//		jQuery('.registerEvent').click(function(event){
//			//行
//			var row = jQuery(event.currentTarget).closest('tr');
//			//お客様番号
//			var customerId = jQuery(row).find('td.customerId').text();
//			//滞在時間
//			var eventTime = jQuery(row).find('input.eventTime').val();
//			//customervisiListTable行番号
//			var index = jQuery(row).find('input.near').val();
//			
//			//スケジュール登録
//			googleMap.getNearLatLng(customerId,eventTime,index);
//			
//			return false;
//		});
	},
	/**
	 * visitListTableの順番を並び替えます
	 * 
	 * @param index
	 *            customerListTableの行番号
	 */
	changevisitList : function(index) {
		
		//customerListTableの指定した行データを取得
		var row = jQuery('#customerListTable tr').eq(index);
		//お客様ID
		var customerId = jQuery(row).find('td.customerId').text();
		//お客様名
		var customerName = jQuery(row).find('td.customerName').text();
		//visitListTableにデータ追加
		visitList.addvisitList(customerId, customerName);
	},
	/**
	 * visitListTableから選択したお客様情報を削除します。
	 * 
	 * @param id
	 *            お客様ID
	 */
	deletevisitList : function(id) {
		var table = document.getElementById('visitListTable');
		var rowNum = table.rows.length;
		for (var i = 1; i < rowNum; i++) {
			var cells = table.rows[i].children;
			for (var j = 0; j < cells.length; j++) {
				console.log(cells[j].innerHTML);
				if (id == cells[j].innerHTML) {
					$('.stripe2 tr:odd').removeClass('odd');
					table.deleteRow(i);
					//行番号修正
					for(var j = 1; j < table.rows.length; j++){
						// TODO いけてないので修正したい
						console.log(table.rows[j].childNodes[0].textContent);
						table.rows[j].childNodes[0].innerHTML = j;
						$('.stripe2 tr:odd').addClass('odd');
					}
					return;
				}
			}
		}
	},
	/**
	 * visitListTableから選択した全てのお客様情報を削除します。
	 * 
	 * @param id
	 *            お客様ID
	 */
	deleteAllVisitList : function() {
		var table = document.getElementById('visitListTable');
		var rowNum = table.rows.length;
		for (var i = 1; i < rowNum; i++) {
			table.deleteRow(1);
		}
	},
	/**
	 * スケジュールに一件イベントを登録します。
	 */
	registerEvent : function(rowNum){
		//行
		var row = jQuery('#visitTable tr').eq(rowNum);
		//お客様番号
		var customerId = jQuery(row).find('td.customerId').text();
		//滞在時間
		var eventTime = jQuery(row).find('input.eventTime').val();
		//customervisiListTable行番号
		var index = jQuery(row).find('input.near').val();
		
		//スケジュール登録
		googleMap.getNearLatLng(customerId,eventTime,index);
	},
	/**
	 * スケジュールに複数のイベントを登録します。
	 */
	registerEvents : function(){		
		index.startLoading();
		var rowLen = $("#visitListTable tbody").children().length;
		var optimizedRoute = googleMap.getOptimizedResult();
		optimizedResult = new Object();
		id = [];
		time = [];
		duration = [];
		for(var i = 1; i<= rowLen;i++){
			var row = jQuery('#visitListTable tr').eq(i);
			//お客様ID
			var customerId = jQuery(row).find('td.customerId').text();
			//滞在時間
			var eventTime = jQuery(row).find('input.eventTime').val();
			id.push(customerId);
			time.push(eventTime);
			duration.push(optimizedRoute.legs[i-1].duration.value);
		}
		optimizedResult['id']=id;
		optimizedResult['time']=time;
		optimizedResult['duration']=duration;
		index.loadMains(optimizedResult);
	}
}