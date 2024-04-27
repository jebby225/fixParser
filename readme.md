# FIX Message Parser

## Introduction

The FIX Message Parser is an application that takes a FIX message in byte array format and validates the format of the message prior parsing it to create a corresponding FIX message object per the message type defined in the message header. A rest API is available for the users to reach the FIX Message Parser.

## Backgroud


***The Encoding***

The FIX (Financial Information eXchange) tag-value encoding is the original encoding used for FIX messages. The encoding uses an integer number known as a tag to identify the field, followed by the “=” character (hexadecimal 0x3D), then the value of that field encoded in a single-byte character set. Each tag-value pair is separated by the Start of Heading control character <SOH> (hexadecimal value 0x01). The tag-value encoding also supports the encoding of binary and multibyte character data in certain encoded data fields that are preceded by a Length field.



***The Format***

A FIX message is a collection of fields that begins with the BeginString(8) field, followed by the BodyLength(9) field, then the MsgType(35) field, and ends with the Checksum(10) field. The message type is identified by the value provided in the MsgType(35) field.


The general format of a message is a standard header followed by the message body fields and terminated with a standard trailer.
Each message is constructed of a stream of tag=value fields with a field delimiter (i.e. "\u0001") between fields in the stream.


***The Example***

The following is a FIX 4.2 NewOrderSingle(35=D) message in classic tagvalue pair format:

```
8=FIX.4.2<SOH>9=251<SOH>35=D<SOH>49=AFUNDMGR<SOH>56=ABROKER<SOH>34=2<SOH>52=2003061501:14:49<SOH>11=12345<SOH>1=111111<SOH>63=0<SOH>64=20030621<SOH>21=3<SOH>110=1000<SOH>111=50000<SOH>55=IBM<SOH>48=459200101<SOH>22=1<SOH>54=1<SOH>60=2003061501:14:49<SOH>38=5000<SOH>40=1<SOH>44=15.75<SOH>15=USD<SOH>59=0<SOH>10=127<SOH>
```

## The Design

The users are required to provide 1 input argument:
* A FIX message (i.e. the message shown in the "example" section) in byte array format.

and a choice of the below two method calls:
1. `FixComponent FixParserService.parseGeneric(byte[]);`
   * _Pros_: Stores tag-value pairs as raw format (byte[]) and thus more efficient. Users have more flexibility to do further manipulation on the returned object.
   * _Cons_: It has no concept of message type; no validation will be done to confirm if all required fields are provided nor if they are of correct data type.
   
2. `FixComponent FixParserService.parseByMessageType(input_F.getBytes());`
    * _Pros_: More strongly typed as it is coded specifically for each FIX message type. Required fields are ready to be used in the correct format as validations are included during the parsing process
    * _Cons_: logic (e.g. required fields, data type) for each message type need to be specifically coded in each child class.

Once the above input and choice have been specified, the program will do the followings:

1. Validate the header and make sure it's in the below format (value for each tag varies)
```
8=FIX.4.2<SOH>9=251<SOH>35=D<SOH>
```
2. Validate the trailer and make sure it's in the below format (value for each tag varies)
```
10=127<SOH>
```
3. Per the choice of the method call:
   * If `FixComponent FixParserService.parseGeneric(byte[]);` is called:
     * the program will parse the FIX message body and store the data in an array of tag-value pair.
     * when going through the FIX message byte array, the parser will check if either the tag or the value is empty.
     * user could display the object as string to show all tags or call `FixComponent.getValueByTag(int tag)` to get the value for a particular tag
   * If `FixComponent FixParserService.parseByMessageType(byte[]);` is called:
     * Per the message type (i.e. the value for tag 35 in the header) defined in the header, a corresponding FIX message object will be created and the body will be parsed accordingly. 
When going through the FIX message byte array, the parser will check if:
       * either the tag or the value is empty
       * the required fields of the corresponding message type is missing
       * the defined values are of the valid type. (values of type String will be stored as byte array to minimize new string creating for performance purpose)
  
     * During the parsing process, all the properties (i.e. the required tags and other tags specified) will be set. All other tags that appear in the message but not specified as properties in the class will be stored in an array of tag-value pair. 
     * user could display the object as string to show all tags or call `FixComponent.getValueByTag(int tag)` to get the value for a particular optional tag (i.e. tags that are stored in the arryay of tag-value pair)
     * Below shows a sample output of a resulting FIX object when presenting as string (in this case, it's a NewOrderMessage object for message type = D):
    ```
    Fix Header {
    fixVersion=FIX.4.2
    bodyLength=251
    msgType=D
    }
    NewOrderMessage {
    clOrdID=12345
    handlInst=3
    ordType=1
    side=1
    symbol=IBM
    transactTime=2003-06-15T01:14:49
    price=15.75
    49=AFUNDMGR
    56=ABROKER
    34=2
    52=2003061501:14:49
    1=111111
    63=0
    64=20030621
    110=1000
    111=50000
    48=459200101
    22=1
    38=5000
    15=USD
    59=0
    }
    Fix Trailer{
    checkSum=127
    }
    ```
4. If the message fails to pass any of the validation mentioned in the steps above, an exception will be thrown and the parser will stop processing the message and return nothing.




## The Limitations
* Required FIX groups and repeated groups are not specifically tested and designed for in this FIX parser.
* It is expected that the input byte array contains a complete FIX message and does not contain any special characters (e.g. newline) other than the character "\u0001"
* The FIX parser only throws exception if not all the required fields are present; it omits the details of which required fields are missing.
* Required tags that are not in repeated group are expected to only have one occurrence in the message; there's no validation against such case.

## Technology
This is an application developed with Gradle, Java 11, and JUnit5.
Users could import the **FixParser.jar** file and utilize the FIX parser


### Running the Application from Command Prompt
 1. In your own application, add the provided **FixParser.jar** file to the library
 2. Import the package as `import org.fixparser.service.*;`
 3. Call either of the below method to retrieve a `FixComponent` object, where it contains all the tags available in the input byte array. Required tags are set in the pre-defined properties if `parseByMessageType` is called
    * `FixParserService.parseGeneric(byte[]);` 
    * `FixParserService.parseByMessageType(input_F.getBytes());`
 4. A sample program `FixParserCaller` is provided separately for demo and benchmark showing purposes. 


### Unit Tests
* Unit tests are included and developed with JUnit 5.
