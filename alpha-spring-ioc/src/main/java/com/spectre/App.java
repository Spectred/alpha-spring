package com.spectre;

import com.spectre.beans.ClientService;
import com.spectre.beans.User;
import com.spectre.beans.service.AccountService;
import com.spectre.dependencies.collections.CollectionsBean;
import com.spectre.dependencies.constructor.ThingOne;
import com.spectre.dependencies.setter.ThingFive;
import com.spectre.dependencies.setter.ThingFour;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        // create and configure beans
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml");

        // retrieve configured instance -Instantiation with a Constructor
        User user = ctx.getBean("user", User.class);

        // bean-alias
        User userAlias = ctx.getBean("user-alias", User.class);

        //Instantiation with a Static Factory Method
        ClientService clientService = ctx.getBean("clientService", ClientService.class);

        // Instantiation by Using an Instance Factory Method
        ClientService clientFactoryService = ctx.getBean("clientFactoryService", ClientService.class);
        AccountService accountFactoryService = ctx.getBean("accountFactoryService", AccountService.class);

        // Constructor-based Dependency Injection
        ThingOne beanOne = ctx.getBean("beanOne", ThingOne.class);
        ThingOne beanOneAnother = ctx.getBean("beanOneAnother", ThingOne.class);
        // Test c
        ThingOne beanOneC = ctx.getBean("beanOneC", ThingOne.class);

        // Setter-based Dependency Injection
        ThingFour beanFour = ctx.getBean("beanFour", ThingFour.class);
        // Test p
        ThingFive beanFive = ctx.getBean("beanFive", ThingFive.class);

        // CollectionsBean
        CollectionsBean collectionsBean = ctx.getBean("collectionsBean", CollectionsBean.class);

        printObjects(user, userAlias, clientService, clientFactoryService, accountFactoryService,
                beanOne, beanOneAnother, beanOneC, beanFour, beanFive, collectionsBean);
    }

    private static void printObjects(Object... objects) {
        for (Object object : objects) {
            System.out.println(object);
        }
    }

}
