var questionnaire = {
		_id : "id", // private 変数 eventIdを格納
		setEventId : function(eventId){
			this._id = eventId;
		},
		sendQuestionnaire : function(){
			$('#questionnaire').empty();
			$('#main').load('./main.html', function() {
				schedule.displaySchedule();
				$('#info').load('./info.html', function() {
					info.setEventId(questionnaire._id);
					$('#outSystem').css('float', "left");
					$('#notDone').css('float', "left");
				});
			});

		}
}