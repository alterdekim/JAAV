package com.alterdekim.fridaapp.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerManager {

    private static final Map<ControllerId, IController> map = new HashMap<>();

    public static void putController(IController controller) {
        if( !map.containsKey(controller.getControllerId()) ) map.put(controller.getControllerId(), controller);
    }

    public static IController getController(ControllerId id) {
        return map.get(id);
    }
}
