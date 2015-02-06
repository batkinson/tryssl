# TrySSL

An extremely simple HTTPS connection diagnostics tool, originally created to
troubleshoot https://jira.codehaus.org/browse/CONTINUUM-2501.

## Requirements

To run the program you'll need:

  * A working Java development kit, version 5 or later
  * Apache Maven 2+

## Building

To build the tool, simply issue the normal maven build mantra:

```
   mvn clean install
```

## Running

To run the tool, issue the following:

```
java -jar tryssl-<version>.jar https://your/url/here
```

By default, it does not validate hostnames or certificates. However, there are
options to enable either of these checks. Just issue the following to see the
options:

```
java -jar tryssl-<version>.jar --help
```
