/**
 * スケジュール管理するためのjsです。
 */
var schedule = {
	displaySchedule : function() {
		$('#schedule').fullCalendar({
			// ヘッダーのタイトルとボタン
			header : {
				// title, prev, next, prevYear, nextYear, today
				left : 'prev,next today',
				center : 'title',
				right : 'month agendaWeek agendaDay'
			},
	        // 列の書式
	        columnFormat: {
	            month: 'ddd',    // 月
	            week: 'D[(]ddd[)]', // 7(月)
	            day: 'D[(]ddd[)]' // 7(月)
	        },
	        // タイトルの書式
	        titleFormat: {
	            month: 'YYYY年 M月',                             // 2013年9月
	            week: 'YYYY年M月D日', // 2013年9月7日 ～ 13日
	            day: 'YYYY年M月D日[(]ddd[)]'                  // 2013年9月7日(火)
	        },
	        // ボタン文字列
	        buttonText: {
	            prev:     '<', // <
	            next:     '>', // >
	            today:    '今日',
	            month:    '月',
	            week:     '週',
	            day:      '日'
	        },
	        // 月名称
	        monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
	        // 月略称
	        monthNamesShort: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
	        // 曜日名称
	        dayNames: ['日曜日', '月曜日', '火曜日', '水曜日', '木曜日', '金曜日', '土曜日'],
	        // 曜日略称
	        dayNamesShort: ['日', '月', '火', '水', '木', '金', '土'],
			defaultView : 'agendaDay',
			// 終日スロットを表示
	        allDaySlot: true,
	        // 終日スロットのタイトル
	        allDayText: '終日',
	        // スロットの時間の書式
	        axisFormat: 'H:mm',
			// 時間の書式
	        timeFormat: 'H:mm',
	        //時間間隔
	        slotDuration:'00:10:00',
	        //スクロール開始時間
	        scrollTime:'09:00:00',
	        //スクロール時間の最大、最小の設定
	        minTime:'00:00:00',
	        maxTime:'24:00:00',
			// jQuery UI theme
			theme : false,
			// 最初の曜日
			firstDay : 1, // 1:月曜日
			// 土曜、日曜を表示
			weekends : true,
			// 週モード (fixed, liquid, variable)
			weekMode : 'fixed',
			// 週数を表示
			weekNumbers : false,
			timezone : 'local',
			
//			editable: true,
			slotEventOverlap : false, // スケジュールが重なったとき、重ねて表示するかどうか
			 // ビュー表示イベント
			viewRender: function(view) {
				span = new Object();
				span['start'] = common.getUTC(view.start._d);
				span['end'] = common.getUTC(view.end._d);
				index.startLoading();
				$.ajax({
					type : "post",
					url : "Customervist/schedule/event",
					data: span,
					success : function(eventSources) {
						$('#schedule').fullCalendar('removeEvents');
						$('#schedule').fullCalendar('addEventSource', eventSources);
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert('表示失敗');
					},
					complete : function(data) {
						index.endLoading();	
					}
				});
			}, 
			dayClick: function(date){ //イベントじゃないところをクリックしたとき(日をクリックしたとき)に実行
			},
			eventClick : function(event,jsEvent, view) { // イベントをクリックしたときに実行
				if(common.judgeId(event.id)){
					//何もしない
				} else{
					$('#info').load('./info.html', function() {
						if(common.compareDate(new Date(event.end))){
							$('#info_notDone').toggle();
						} else {
						}
						$('#info_tabs').tabs();
						//イベント情報を取得します。
						index.startLoading();
						$.ajax({
							type : "post",
							data : { "eventId" : event.id},
							url : "Customervist/schedule/detail",
							success : function(eventInfo){
								console.log(eventInfo.eventStartTime);
								console.log(eventInfo.eventEndTime);
								//ローカルサーバーとクラウドサーバーの差異がでてくる
								$('#titleDetail').text(eventInfo.eventName);
								$('#startDetail').text(common.toLocaleString(common.getJST(eventInfo.eventStartTime)));
								$('#endDetail').text(common.toLocaleString(common.getJST(eventInfo.eventEndTime)));
								$('#locationDetail').text(eventInfo.eventLocation);
								
								//お客様口座情報を取得します。
								$.ajax({
									type : "post",
									data : { "eventId" : event.id},
									url : "Customervist/customer/accounts",
									success : function(accounts){
										for (var i = 0; i < accounts.length; i++) {
											$("#accountTable tbody")
													.append(
															$('<tr>')
																	.append(
																			$('<td class="branch">').text(
																					accounts[i]['branch']))
																	.append(
																			$('<td class="deposit">')
																					.text(
																							accounts[i]['deposit']))
																	.append(
																			$('<td class="accountId">')
																					.text(
																							accounts[i]['accountId']))
																							.append(
																									$('<td class="balance" style="text-align: right">')
																											.text(
																													common.addComma(accounts[i]['balance']))));
										}
										$('.stripe tr:odd').addClass('odd');
									},
									error : function(XMLHttpRequest, textStatus, errorThrown) {
										alert('取得に失敗しました。');
									},
									complete : function(data) {
									}
								});
							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {
								alert('取得に失敗しました。');
							},
							complete : function(data) {
								//loadingを終了します。
								index.endLoading();
							}
						});

						//Closeボタンが押下されたとき実行します。
						$('#close').click(function(){
							$('#info').empty();
						});
						//編集ボタンが押下されたとき実行します。
						$('#edit').click(function(){
							$('#eventTitle').val(event.title);
							$('#eventStart').val(common.toLocaleString(new Date(Date.parse(event.start))));
							$('#eventEnd').val(common.toLocaleString(new Date(Date.parse(event.end))));
							$('#eventStart').datetimepicker();
							$('#eventEnd').datetimepicker();
						});
						//顧客詳細情報ボタンが押下されたとき実行します。
						$('#customerInfo').click(function(){
							alert('顧客情報詳細へ');
						});
						//取引履歴ボタンが押下されたとき実行します。
						$('#tranzaction').click(function(){
							alert('取引履歴へ');
						});
						//お客様アンケートボタンが押下されたとき実行します。
						$('#questionnaire').click(function(){
//							$('#main').empty();
//							$('#info').empty();
//							$('#questionnaire').load('./questionnaire.html', function() {
//								questionnaire.setEventId(info._id);
//							});
							alert('お客様アンケートへ');
						});
						//訪問結果報告ボタンが押下されたとき実行します。
						$('#report').click(function(){
							if(confirm("交渉報告を行ってもよろしいでしょうか？")) {
								index.startLoading();
								$.ajax({
									type : "get",
									url : "Customervist/report",
									success : function(){
										alert('報告完了しました。');
									},
									error : function(XMLHttpRequest, textStatus, errorThrown) {
										alert('送信に失敗しました。');
									},
									complete : function(data) {
										//loadingを終了します。
										index.endLoading();
									}
								});
							}
						});
						//UpdateButtonが押下されたとき実行されます。
						$("#updateButton").click(function(){
						updatedEvent = new Object;
						updatedEvent['id'] = event.id;
						updatedEvent['title'] = $('#eventTitle').val();
						updatedEvent['start'] = $('#eventStart').val();
						updatedEvent['end'] = $('#eventEnd').val();
						event.title = $('#eventTitle').val();
						event.start = $('#eventStart').val();
						event.end = $('#eventEnd').val();
						index.startLoading();
						$.ajax({
							type : "post",
							url : "Customervist/schedule/update",
							data: updatedEvent,
							success : function() {
								alert('更新が終了しました。');
								$('#schedule').fullCalendar('updateEvent', event);
							},
							error : function(XMLHttpRequest, textStatus, errorThrown) {
								alert('失敗');
								$('#info').empty();
							},
							complete : function(data) {
								index.endLoading();
								$('#info').empty();
							}
						});
					});
					});
				}
			},
			eventDrop : function(event, delta, revertFunc) {
//				if(common.compareDate(new Date(event.start))){
//					revertFunc();
//				}else{
//					$('#update').load('./update.html', function() {
//						$('#eventTitle').val(event.title);
//						$('#eventStart').val(common.toLocaleString(new Date(Date.parse(event.start))));
//						$('#eventEnd').val(common.toLocaleString(new Date(Date.parse(event.end))));
//						$('#eventStart').datetimepicker();
//						$('#eventEnd').datetimepicker();
//						$("#updateCancel").click(function(){
//							  //実行される
//							revertFunc();
//							$('#update').empty();
//						});
//						$("#updateButton").click(function(){
//							updatedEvent = new Object;
//							updatedEvent['id'] = event.id;
//							updatedEvent['title'] = $('#eventTitle').val();
//							updatedEvent['start'] = $('#eventStart').val();
//							updatedEvent['end'] = $('#eventEnd').val();
//							event.title = $('#eventTitle').val();
//							event.start = $('#eventStart').val();
//							event.end = $('#eventEnd').val();
//							index.startLoading();
//							$.ajax({
//								type : "post",
//								url : "Customervist/schedule/update",
//								data: updatedEvent,
//								success : function() {
//									alert('更新が終了しました。');
//									$('#schedule').fullCalendar('updateEvent', event);
//								},
//								error : function(XMLHttpRequest, textStatus, errorThrown) {
//									alert('失敗');
//									revertFunc();
//								},
//								complete : function(data) {
//									index.endLoading();
//									$('#update').empty();
//								}
//							});
//						});
//					});
//				}
			},
			//外部からドラッグしてくる
			editable: true,
			droppable: true,
//			dropAccept: '.selectCustomer',
		    drop: function(date) {
		    	// retrieve the dropped element's stored Event Object
		    	var originalEventObject = $(this).data('eventObject');
		    	// we need to copy it, so that multiple events don't have a reference to the same object
		    	var copiedEventObject = $.extend({}, originalEventObject);
		    	// assign it the date that was reported
		    	copiedEventObject.start = date._d;

		    	copiedEventObject.end = common.getEnd(date._d);
		    	
		    	// render the event on the calendar
		    	// the last `true` argument determines if the event "sticks" (http://arshaw.com/fullcalendar/docs/event_rendering/renderEvent/)
		    	$('#schedule').fullCalendar('renderEvent', copiedEventObject, true);
		    }

			//イベントをJSONで渡す
//			events : schedule
		});
	},
	deleteSchedule : function() {
		$('#schedule').fullCalendar('destroy');
	}
}