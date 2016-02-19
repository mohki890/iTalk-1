package com.example.gagan.italk;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gagan on 17/5/15.
 */
public class ContentConversion {
    //length of key in AES
    //Do we need to generate Key Everytime(Speed and Security)
    private static String LOG=ContentConversion.class.getSimpleName();


    /***Success and Error Code ****/
    public static final int SETKEY_DONE=0;
    public static final int SETKEY_NULL=1;
    public static final int SETKEY_MAXDIGITNOTMATCH=2;
    public static final int SETKEY_NOSUCHALGO=3;
    public static final int SETKEY_NOSUCHPADDING=4;
    public static final int SETKEY_INVALIDKEY=5;


    //Data Member For AES
    private String enc_type="AES";
    private final int key_size=16;
    private Key key;
    private Cipher cipherE,cipherD;
    private boolean isOk=false;





    //Methods
    public int setKey(String str)
    {
        Log.e(LOG,"Set Key Start ");
        if(str==null) return SETKEY_NULL;
        if(str.length()>key_size) return SETKEY_MAXDIGITNOTMATCH;
        byte[] key_array=new byte[key_size];
        for(int i=0;i<str.length();i++)
            key_array[i]=(byte)str.charAt(i);
        key=new SecretKeySpec(key_array,enc_type);
        isOk=false;
        try {
            cipherE=Cipher.getInstance(enc_type);
            cipherD=Cipher.getInstance(enc_type);

        } catch (NoSuchAlgorithmException e) {

            destroyKey();
            Log.e(LOG,e.toString());
            return SETKEY_NOSUCHALGO;
        } catch (NoSuchPaddingException e) {
            destroyKey();
            Log.e(LOG,e.toString());
            return SETKEY_NOSUCHPADDING;
        }

        try {
            cipherE.init(Cipher.ENCRYPT_MODE,key);
            cipherD.init(Cipher.DECRYPT_MODE,key);
        } catch (InvalidKeyException e) {
            Log.e(LOG,e.toString());
            destroyKey();
            return SETKEY_INVALIDKEY;
        }

        isOk=true;
        Log.e(LOG,"Set Key Done");

        return SETKEY_DONE;

    }

    public void destroyKey()
    {
        key=null;
        isOk=false;
    }


    public String encrypt(String data)
    {
        if(data==null) {
            Log.e(LOG,"Encrypt : Data is NULL");

            return null;
        }
        if(isOk)
        {
            byte[] b=null;
            try {
                b= cipherE.doFinal(data.getBytes());
            } catch (IllegalBlockSizeException e) {
                Log.e(LOG,e.toString());
                return null;
            } catch (BadPaddingException e) {
                Log.e(LOG,e.toString());
                return null;
            }
            String s=new String(Base64.encode(b,Base64.DEFAULT)); //RFC 2045
            return s;
        }
        Log.e(LOG,"Encrypt Returning NULL, isOK="+isOk);

        return null;
    }

    public String decrypt(String enc)
    {
        if(enc==null) return null;
        if(isOk)
        {
            byte[] dec=null;
            try {
               dec= cipherD.doFinal(Base64.decode(enc,Base64.DEFAULT));
            } catch (IllegalBlockSizeException e) {
                Log.e(LOG,e.toString());
                return null;
            } catch (BadPaddingException e) {
                Log.e(LOG,e.toString());
                return null;
            }
            if(dec==null) return null;
            return new String(dec);
        }
        return null;
    }


}
