package controller;


import service.OrderService;
import view.MainView;
import view.MerchantView;

import java.util.List;

public class MainController implements Controller {

    // Not a View interface at the moment, i will see if i need that later
    MainView view;
    List<Controller> subControllers;

    public MainController(MainView view, List<Controller> subControllers) {
        this.view = view;
        this.subControllers = subControllers;
    }

    public void run(){
        while(true){
            // -1 here because the menu gets a selection starting with index 1
            int index = view.displaySubControllersAndGetSelection(subControllers) - 1;

            if(index == subControllers.size()){
                break;
            }
            subControllers.get(index).run();
        }


    }

    @Override
    public String getName() {
        return "Main Menu";
    }

}
