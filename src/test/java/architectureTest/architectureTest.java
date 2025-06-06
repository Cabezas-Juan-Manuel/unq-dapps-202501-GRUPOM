package architectureTest;

import ar.edu.unq.pronostico.deportivo.model.playerFactory.PlayerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {
    @Test
    void testLayerChecks(){
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("ar.edu.unq.pronostico.deportivo.webservice")
                .layer("Service").definedBy("ar.edu.unq.pronostico.deportivo.service")
                .layer("Persistence").definedBy("ar.edu.unq.pronostico.deportivo.repositories")
                .layer("Model").definedBy("ar.edu.unq.pronostico.deportivo.model")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Model").mayOnlyBeAccessedByLayers("Service");
    }

    @Test
    void testControllersAreTransactional(){
        classes().that().areAssignableTo(RestController.class)
                .should().onlyHaveDependentClassesThat().areAnnotatedWith(Transactional.class);
    }

    @Test
    void servicesAreInTheRightPackages(){
        classes().that().haveSimpleNameEndingWith("Service")
                .should().resideInAPackage("ar.edu.unq.pronostico.deportivo.service");
    }

    @Test
    void controllersAreInTheRightPackages(){
        classes().that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("ar.edu.unq.pronostico.deportivo.webservice");
    }

    @Test
    void repositoriesAreInTheRightPackages(){
        classes().that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("ar.edu.unq.pronostico.deportivo.repositories");
    }

    @Test
    void allFactoryNameClassesAreCorrect(){
        classes().that().areAssignableTo(PlayerFactory.class)
                .should().haveSimpleNameEndingWith("Factory");
    }
}



