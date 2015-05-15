package com.freetmp.mbg.i18n;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * <B>AppResources</B> is a convenience wrapper for accessing resource
 * store in a <I>ResourceBundle<I>.
 * 
 * @version Ver 1.1 2009-4-13 modify
 * @since Application Ver 1.1 <br>
 * 
 * 1.1 keeping only the getString and getInteger method. <br>
 * 1.0 JHotDraw7
 */
public class Resources {

    /**The wrapped resource bundle. */
    protected final ResourceBundle resource;

    /*
     * Instantiates a new resourcebundleutil which wraps the provided resource
     * bundle.
     * @param r the ResourceBundle
     */
    public Resources(ResourceBundle r) {
        resource = r;
    }

    /*
     * use utf-8 charset to decode the properties file
     * @param baseName resource properties file name
     * @param locale the locale to parse the resource
     */
    public Resources(String baseName,Locale locale){
        ResourceBundle rb = ResourceBundle.getBundle(baseName,locale,new UTF8Control());
        this.resource = rb;
    }

    /*
     * use specified class loader to load i18n resources
     * @param s resource properties file name
     * @param locale the locale to parse the resource
     * @param loader class loader
     */
    public Resources(String s, Locale locale, ClassLoader loader) {
        ResourceBundle rb = ResourceBundle.getBundle(s,locale,loader,new UTF8Control());
        this.resource = rb;
    }

    /*
     * Gets a string from the ResourceBundles.
     * <br> Convenience method to save casting.
     * 
     * @param key the key of the properties.
     * 
     * @return the value of the property. Return empty string if the value is not found.
     */
    public String getString(String key){
        try {
            return resource.getString(key);
        } catch (MissingResourceException e) {
            return "";
        }
    }

    /*
     * a convenience mentod to get all strings as a sentence or phrase.
     * @since 1.1 
     */
    public String getString(String ...keys) {
        StringBuilder sb = new StringBuilder();
        for(String key:keys){
            sb.append(getString(key));
            sb.append(" ");
        }
        return sb.toString();
    }
    public String getTips(String key) {
        String tips = key+".tips";
        String retval = getString(tips);
        if(retval.equals(tips)){
            retval= getString(key);
        }
        return retval;
    }
    /*
     * Gets the integer from the properties.
     * 
     * @param key the key of the property.
     * 
     * @return the value of the key. return -1 if the value is not found.
     */
    public Integer getInteger(String key){
        try {
            return Integer.valueOf(resource.getString(key));
        } catch (MissingResourceException e) {
            return new Integer(-1);
        }
    }

    /*
     * Gets the int.
     * 
     * @param key the key
     * 
     * @return the int
     */
    public int getInt(String key){
        try {
            return Integer.parseInt(resource.getString(key));
        } catch (NumberFormatException e) {
            return -1; // modify here if you need to throw a Exception here
        }
    }

    /*
     * Gets the bundle.
     * 
     * @return the bundle
     */
    public ResourceBundle getBundle(){
        return resource;
    }

    /*
     * Gets the keys.
     * 
     * @return the keys
     */
    public Enumeration<String> getKeys(){
        return resource.getKeys();
    }

    /*
     * Gets a resource string formatted with MessageFormat.
     * 
     * @param key the key
     * @param argument the argument
     * 
     * @return Formatted stirng.
     */
    public String getFormatted(String key, String argument) {
        return MessageFormat.format(resource.getString(key), new Object[] {argument});
    }

    /*
     * Gets a resource string formatted with MessageFormat.
     * 
     * @param key the key
     * @param arguments the arguments
     * 
     * @return Formatted stirng.
     */
    public String getFormatted(String key, Object... arguments) {
        return MessageFormat.format(resource.getString(key), arguments);
    }

    @Override
    public String toString() {
        return super.toString()+"["+resource+"]";
    }

    public char getChar(String string) {
        try {
            return resource.getString(string).charAt(0);
        } catch (IndexOutOfBoundsException  e) {
            return Character.MIN_VALUE;
        }
    }
}