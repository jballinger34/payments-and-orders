package view;

import controller.Controller;

import java.util.List;

public class MainView {
    private final UserIO io;

    public MainView(UserIO io) {
        this.io = io;
    }

    public int displaySubControllersAndGetSelection(List<Controller> controllers) {
        io.print("=================");
        io.print("Welcome");
        for(int i = 0; i < controllers.size(); i++){
            io.print((i+1)+". " + controllers.get(i).getName());
        }
        io.print((controllers.size() + 1)+ ". Exit");

        return io.readInt("Enter selection:", 1, controllers.size()+1);
    }


}
