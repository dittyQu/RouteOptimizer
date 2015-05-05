var common = {
	/**
	 * 現在時刻との時間を比較します。
	 * 
	 * @param date
	 *            比較したい時間
	 * @returns {Boolean} 現在時刻より過去ならtrueを未来ならfalseを返します。
	 */
	compareDate : function(date) {
		var now = new Date();
		if (now > date) {
			return true;
		} else {
			return false;
		}
	},
	/**
	 * IDがmoveで始まるか確認します。
	 * 
	 * @param id
	 *            eventId
	 * @returns {Boolean} イベントが移動ならばtrue、移動でなければfalseを返します。
	 */
	judgeId : function(id) {
		if (id.substring(0, 4) == 'move') {
			return true;
		} else {
			return false;
		}
	},
	/**
	 * JST時間からUTC時間のミリ秒を取得する
	 * 
	 * @param d
	 *            JST時間
	 * @returns UTCミリ秒
	 */
	getUTC : function(d) {
		return d.getTime() + (d.getTimezoneOffset() * 60000);
	},
	/**
	 * UTC時間からJST時間のミリ秒を取得する
	 * 
	 * @param d
	 *            JST時間
	 * @returns UTCミリ秒
	 */
	getJST : function(d) {
		return new Date(d + (new Date().getTimezoneOffset() * 60000));
	},
	/**
	 * 日付の文字列を変換します。
	 * 
	 * @param date
	 *            日付
	 * @returns {String}
	 */
	toLocaleString : function(date) {
		return [ date.getFullYear(), common.toDoubleDigits(date.getMonth() + 1), common.toDoubleDigits(date.getDate()) ]
				.join('/')
				+ ' ' + common.toDoubleDigits(date.getHours()) + ':' + common.toDoubleDigits(date.getMinutes());
	},
	/**
	 * yyyyMMddHHmmを返します
	 * @param date 日付
	 * @returns
	 */
	formatDate : function(date){
		return date.getFullYear() + common.toDoubleDigits(date.getMonth() + 1) + common.toDoubleDigits(date.getDate()) + common.toDoubleDigits(date.getHours()) + common.toDoubleDigits(date.getMinutes());
	},
	/**
	 * スケジュールの終了時間を求めます。デフォルトは2時間です。
	 * @param date 開始時間
	 * @returns {Date} 終了時間
	 */
	getEnd : function(date){
		var miliSecond = date.getTime();
		//デフォルトは2時間
		miliSecond = miliSecond + 2*60*60*1000;
		return new Date(miliSecond);
	},
	/**
	 * 日付、時間、分の桁を0埋めします。
	 * @param num 0埋めしたい時間
	 * @returns {String}
	 */
	toDoubleDigits : function(num) {
		num += "";
		if (num.length === 1) {
			num = "0" + num;
		}
		return num;
	},
	/**
	 * あたえられた文字列に対しカンマをつけます
	 * @param value 金額
	 * @returns カンマ付き金額
	 */
	addComma : function(value){
		return String(value).replace( /(\d)(?=(\d\d\d)+(?!\d))/g, '$1,' );
	}
}