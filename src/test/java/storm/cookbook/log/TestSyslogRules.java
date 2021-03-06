package storm.cookbook.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

import storm.cookbook.log.model.LogEntry;

public class TestSyslogRules extends StormTestCase {

	private StatelessKnowledgeSession ksession;

	@Before
	public void setupDrools(){
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//		kbuilder.add( ResourceFactory.newClassPathResource( "/resources/Syslog.drl",
//		              getClass() ), ResourceType.DRL );
		kbuilder.add( ResourceFactory.newClassPathResource("resources/Syslog.drl"),
				ResourceType.DRL);
		if ( kbuilder.hasErrors() ) {
		    fail(kbuilder.getErrors().toString());
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		ksession = kbase.newStatelessKnowledgeSession();
	}

	@Test
	public void testHostCorrection() throws IOException {
		LogEntry entry = getEntry();
		entry.setHost("localhost");
		ksession.execute( entry );
		assertEquals("localhost.example.com",entry.getHost());
	}

	@Test
	public void testNonHostCorrection() throws IOException {
		LogEntry entry = getEntry();
		ksession.execute( entry );
		assertEquals("hdp01.localdomain",entry.getHost());
	}

	@Test
	public void testFilter() throws IOException {
		LogEntry entry = getEntry();
		entry.setType("other");
		ksession.execute( entry );
		assertEquals(true,entry.isFilter());
	}

	@Test
	public void testDontFilter() throws IOException {
		LogEntry entry = getEntry();
		ksession.execute( entry );
		assertEquals(false,entry.isFilter());
	}

}
