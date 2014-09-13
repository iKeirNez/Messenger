Messenger
=============

Messenger allows plugin developers to easily create editable language / format files for their users.


Setup
-------
Include Messenger in your project and instantiate it when your plugin loads:

```java
public void onEnable() {
    Messenger.load(new File(getDataFolder(), "language.properties"));
}
```

The above must be called for modified values to be loaded and default values to be saved to the config. If not called, it will always return default values.


Defaults
-------

Set up defaults for your plugin. Defaults will be saved to the language file if they do not exist when load() is called. See example values in the Messenger class to see how this is done.

Sending
-------

Before messages are sent, you must call ```Messenger.load(File);```

Using String constants then allows easy sending of these messages to any CommandSender:

```java
CommandSender cs = ...
Messenger.NO_PERMISSION.send(cs);
```

If you have Objects to use to format the message, you may call it like so:

```java
String value1 = ...
String value2 = ...
CommandSender cs = ...
Messenger.NO_PERMISSION.send(cs, value1, value2);
```

Learn more about message formatting in the section below.

Formatting
-------

Messages can be sent with a prefix in front of them. You can change the prefix by changing the default value of PREFIX in Messenger.

You can disable a prefix for a message by setting the prefix boolean to false, see EXAMPLE_NO_PREFIX for an example, prefixes can be disabled altogether by removing the PREFIX enum key.

All messages support the use of ```&``` to denote color codes, such as ```&e``` for yellow, where color codes can be found [here](http://ess.khhq.net/mc/).

All messages support the use of [Java's String.format](http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#format%28java.lang.String,%20java.lang.Object...%29) method to insert variable data into messages.

For example, the following message could be included in the plugin:

```java
public enum Messenger {

...

FORMATTED("Your name is: %s"),

...
```

The message could then be formatted when a CommandSender sends a command by calling:

```java
CommandSender cs = ...
String name = cs.getName();
Messenger.FORMATTED.send(cs, name);
```

In general, it is good practice to use String variables when formatting so that they can easily be reorganized by a user.

To see more examples of formatting messages, see the example defaults in Messenger.java
