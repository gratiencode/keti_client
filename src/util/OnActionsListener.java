/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javafx.event.Event;

/**
 *
 * @author eroot
 */
public interface OnActionsListener {
    public void onLogin(LoginResult lr);
    public void onAction(Object source, Event evet);   
}
