/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.UUID;

/**
 *
 * @author eroot
 */
public class DataId {
    public static String generate(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
