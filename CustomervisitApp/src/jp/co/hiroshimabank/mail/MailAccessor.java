/**
 * メール機能を利用する際必要なデータを取得します
 */
package jp.co.hiroshimabank.mail;

import java.util.Set;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;

/**
 * @author IBM梅沢
 *
 */
public class MailAccessor {

	/** ユーザID */
	private String username;
	/** パスワード */
	private String password;

	private String mailKey = null;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public MailAccessor() {
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
		Set<String> keys = obj.keySet();
		for (String key : keys) {
			if (key.contains("sendgrid")) {
				mailKey = key;
			}
		}
		if (mailKey == null) {
			throw new MailAccessException("Key is null");
		}
		// Parsing the parameters out of the VCAP JSON document
		BasicDBList list = (BasicDBList) obj.get(mailKey);
		obj = (BasicDBObject) list.get("0");
		obj = (BasicDBObject) obj.get("credentials");
		username = (String) obj.get("username");
		password = (String) obj.get("password");
	}

}
