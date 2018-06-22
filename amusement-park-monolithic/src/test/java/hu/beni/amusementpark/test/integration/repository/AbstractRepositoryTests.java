package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.assertSQLStatements;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class AbstractRepositoryTests {

	@Autowired
	private Environment environment;

	protected long insert;
	protected long select;
	protected long update;
	protected long delete;

	protected void assertStatements() {
		assertSQLStatements(select, insert, update, delete);
	}

	protected void incrementSelectIfOracleDBProfileActive() {
		if (Arrays.asList(environment.getActiveProfiles()).contains(ORACLE_DB)) {
			select++;
		}
	}

}
