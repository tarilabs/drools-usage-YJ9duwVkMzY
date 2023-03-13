
package org.drools.demo;

import static org.junit.Assert.assertEquals;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleTest {
    static final Logger LOG = LoggerFactory.getLogger(RuleTest.class);

    @Test
    public void test() {
        KieContainer kContainer = createKieContainer();

        LOG.info("Creating kieBase");
        KieBase kieBase = kContainer.getKieBase();

        LOG.info("There should be rules: ");
        for ( KiePackage kp : kieBase.getKiePackages() ) {
            for (Rule rule : kp.getRules()) {
                LOG.info("kp " + kp + " rule " + rule.getName());
            }
        }

        LOG.info("Creating kieSession");
        KieSession session = kieBase.newKieSession();

        try {
            Fact fact = new Fact();
            fact.getMap().put("fileName", "abc.11032023.xml");
            fact.getMap().put("documentType", "Invoice");
            session.getAgenda().getAgendaGroup("endPoint1").setFocus();
            session.insert(fact);
            session.fireAllRules();
            session.dispose();

            // Rule R1 matching the fact has the same values
            assertEquals(1, fact.getActions().size());
            System.out.println(fact.getActions());
        } finally {
            session.dispose();
        }
    }

    private KieContainer createKieContainer() {
        // Programmatically collect resources and build a KieContainer
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        String packagePath = "org.drools.demo".replace(".", "/");
        kfs.write("src/main/resources/" + packagePath + "/rules.drl",
                  ks.getResources().newInputStreamResource(this.getClass().getClassLoader().getResourceAsStream(packagePath + "/rules.drl")));
        ReleaseId releaseId = ks.newReleaseId("org.drools.demo", "drools-usage-YJ9duwVkMzY", "1.0-SNAPSHOT");
        kfs.generateAndWritePomXML(releaseId);
        ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        return ks.newKieContainer(releaseId);
    }
}