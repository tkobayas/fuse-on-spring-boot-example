package com.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.camel.Processor;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ProcessorCreator {

    Logger logger = LoggerFactory.getLogger(ProcessorCreator.class);

    @Bean
    public Processor kieClientProcessor() {
        return exchange -> {
            KieServicesConfiguration kieConf = KieServicesFactory.newRestConfiguration(Constants.BASE_URL, Constants.USERNAME, Constants.PASSWORD);

//            kieConf.setMarshallingFormat(MarshallingFormat.JAXB);
//                  kieConf.setMarshallingFormat(MarshallingFormat.JSON);
                  kieConf.setMarshallingFormat(MarshallingFormat.XSTREAM);

            kieConf.setTimeout(60000L);
            HashSet<Class<?>> extraClasses = new HashSet<Class<?>>();
            extraClasses.add(Person.class);
            kieConf.addExtraClasses(extraClasses);

            Object body = exchange.getIn().getBody();
            logger.info("Before calling kie-server : body = " + body);

            KieCommands kieCommands = KieServices.get().getCommands();
            List<Command<?>> commands = new ArrayList<>();
            commands.add(kieCommands.newInsert(exchange.getIn().getBody(), "Person"));
            commands.add(kieCommands.newFireAllRules());

            BatchExecutionCommand batchCommand =
                    KieServices.get()
                               .getCommands()
                               .newBatchExecution(commands, Constants.KSESSION_NAME);

            RuleServicesClient kieClient =
                    KieServicesFactory.newKieServicesClient(kieConf)
                                      .getServicesClient(RuleServicesClient.class);
            ServiceResponse<ExecutionResults> response =
                    kieClient.executeCommandsWithResults(Constants.CONTAINER_ID, batchCommand);

            if (response.getType() != ResponseType.SUCCESS) {
                throw new RuntimeException("Error calling KieServer:" + response.getType().toString());
            }

            ExecutionResults results = response.getResult();
            if (results == null) {
                logger.warn("=== no results");
            }
            Person person = (Person) results.getValue("Person");
            exchange.getIn().setBody(person);
        };
    }
}
