package EHRSystem;

import java.io.*;
import java.util.*;
import java.security.MessageDigest;

public class Encryption {

    String encrypt(String input) {
        
        
        MessageDigest md;
        
        StringBuffer out = new StringBuffer();;
        
        try{
        md = MessageDigest.getInstance("SHA-256");
        
            md.update(input.getBytes());
        
        byte[] con = md.digest();
        
        
        for(int i=0; i<con.length; i++){
            out.append(Integer.toString((con[i] & 0xff) + 0x100, 16).substring(1));
        }
        
            }
        catch(Exception e){}
    return out.toString();
    }
}