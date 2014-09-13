/*
* Messenger
* Copyright (C) 2014 Puzl Inc.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.puzlinc.messenger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to handle language values.
 * This version achieves very simple language files by making use of enums.
 * It also uses properties files instead of yaml files for message storing.
 *
 * @author Keir Nellyer
 */
public enum Messenger {

    // Below are some values you should probably modify

    PREFIX("&7[&6MyPlugin&7] &f", false), // prefixes shouldn't be prefixed :)
    EXAMPLE_SIMPLE("This is the default value"),
    EXAMPLE_FORMATTED("This is a string with some data in it: %s"),
    EXAMPLE_COLOR("This is a string with some &ccolor in it"),
    EXAMPLE_NO_PREFIX("This is a string which will not have a prefix attached to it", false);

    // Code after this point shouldn't need to be touched :)

    private static final String PREFIX_KEY = "PREFIX";
    private static boolean hasPrefixCacheValue = true;

    private final String defaultValue;
    private String currentValue;
    private boolean prefix;

    private Messenger(String defaultValue){
        this(defaultValue, true);
    }

    private Messenger(String defaultValue, boolean prefix){
        this.defaultValue = defaultValue;
        this.currentValue = ChatColor.translateAlternateColorCodes('&', defaultValue);
        this.prefix = prefix;
    }

    /**
     * Returns the default value which will be written to the config if the value doesn't exist.
     *
     * @return The default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Will a prefix be attached to the message when retrieved?
     *
     * @return prefix prefix status of the message
     */
    public boolean isPrefix() {
        return prefix;
    }

    /**
     * Sets whether or not a prefix will be attached to retrieved messages.
     *
     * @param prefix new prefix boolean value
     */
    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the current value contained in the language file with a prefix.
     *
     * @param args Arguments which should be used when formatting the value, see String#format(String, Object...)
     * @return The current value, formatted using the provided arguments
     */
    public String getCurrentValue(Object... args){
        String message = String.format(currentValue, args);

        if (prefix && hasPrefixCacheValue && !name().equals(PREFIX_KEY)){ // prevents infinite loop
            try {
                message = valueOf(PREFIX_KEY).getCurrentValue() + message;
            } catch (IllegalArgumentException ex){
                // if enum didn't exist, we simply won't prefix the message and keep a cache of this value not existing, so we don't have too many exceptions being thrown.
                hasPrefixCacheValue = false;
            }
        }

        return message;
    }

    /**
     * Sends a message to a commandSender using the current value contained in the language file.
     *
     * @param commandSender The CommandSender to send the formatted message to
     * @param args Arguments which should be used when formatting the value, see String#format(String, Object...)
     */
    public void send(CommandSender commandSender, Object... args){
        commandSender.sendMessage(getCurrentValue(args));
    }

    /**
     * Loads the language file and saves defaults to the file if they don't exist.
     *
     * @param languageFile The file to load (or save) the language values
     */
    public static void load(File languageFile){
        load(languageFile, null);
    }

    /**
     * Loads the language file and saves defaults to the file if they don't exist.
     *
     * @param languageFile The file to load (or save) the language values
     * @param header The header message to be displayed at the top of the language file, null for no header
     */
    public static void load(File languageFile, String header){
        try {
            languageFile.createNewFile(); // make sure the file exists
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(languageFile));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Messenger messenger : values()){
            String key = getKey(messenger);

            if (properties.containsKey(key)){ // load from config
                messenger.currentValue = ChatColor.translateAlternateColorCodes('&', properties.getProperty(key));
            } else { // set default value
                properties.setProperty(key, messenger.getDefaultValue());
            }
        }

        try {
            properties.store(new FileOutputStream(languageFile), header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getKey(Messenger messenger){
        return capitalise(messenger.name().replaceAll("_", " ")).replaceAll(" ", "");
    }

    private static String capitalise(String string){
        string = string.toLowerCase();

        String[] parts = string.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < parts.length; i++){
            String part = parts[i];

            if (i == 0){
                stringBuilder.append(part);
            } else {
                stringBuilder.append(Character.toUpperCase(part.charAt(0)));
                stringBuilder.append(part.substring(1, part.length()));
            }

            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

}
