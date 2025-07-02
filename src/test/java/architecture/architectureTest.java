package architecture;

import ar.edu.unq.pronostico.deportivo.model.playerFactory.PlayerFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;

class ArchitectureTest {
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



