package hu.beni.amusementpark.helper;

import com.vladmihalcea.sql.SQLStatementCountValidator;

public class MySQLStatementCountValidator {

	public static void reset() {
		SQLStatementCountValidator.reset();
	}

	public static void assertSQLStatements(long select, long insert, long update, long delete) {
		SQLStatementCountValidator.assertSelectCount(select);
		SQLStatementCountValidator.assertInsertCount(insert);
		SQLStatementCountValidator.assertUpdateCount(update);
		SQLStatementCountValidator.assertDeleteCount(delete);
	}

}
