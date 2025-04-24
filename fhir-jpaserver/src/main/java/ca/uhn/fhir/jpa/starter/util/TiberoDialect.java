package ca.uhn.fhir.jpa.starter.util;

import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.SQL2008StandardLimitHandler;

public class TiberoDialect extends Oracle12cDialect {
//	@Override
//	public String getQuerySequencesString() {
//		return "select sequence_name from all_sequences";
//	}

//	@Override
//	public LimitHandler getLimitHandler() {
//		return SQL2008StandardLimitHandler.INSTANCE;
//	}
}
