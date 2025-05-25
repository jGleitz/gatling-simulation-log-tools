parser grammar GatlingSimulationLogParser;

// we operate on raw bytes, produced by [ByteTokenSource]
tokens {
    BYTE
}
options {
    TokenLabelType=ByteToken;
}

@header {
    import de.joshuagleitze.gatling.simulationlog.parser.tokens.ByteToken
}

runRecord returns [scenarioNames: List<StringContext>, assertions: List<ByteBufferContext>]:
    header[0]
    gatlingVersion=string
    simulationClassName=string
    startEpochMillis=long
    runDescription=string
    scenarioCount=int
    { $scenarioNames = List($scenarioCount.value!!) { string() } }
    assertionCount=int
    { $assertions = List($assertionCount.value!!) { byteBuffer() } };

event:
    userEvent
    | responseEvent
    | groupEvent
    | errorEvent;

responseEvent:
    header[1]
    groups=groupHierarchy
    requestName=cachedString
    startSimulationMs=int
    endSimulationMs=int
    isOk=boolean
    message=cachedString;

userEvent:
    header[2]
    scenarioIndex=int
    isStart=boolean
    simulationMs=int;

groupEvent:
    header[3]
    groups=groupHierarchy
    startSimulationMs=int
    endSimulationMs=int
    cumulatedResponseTimeMs=int
    isOk=boolean;

errorEvent:
    header[4]
    message=cachedString
    simulationMs=int;

header[expectedValue: Byte]:
    byte
    { $byte.value == $expectedValue }?;

groupHierarchy returns [groups: List<CachedStringContext>]:
    groupCount=int
    { $groups = List($groupCount.value!!) { cachedString() } };

cachedString:
    cacheIndex=int
    (
        { $cacheIndex.value!! < 0 }?
    |
        { $cacheIndex.value!! > 0 }?
        value=string
    );

string returns [value: String]:
    length=int
    (
        {$length.value!! == 0}?
        { $value = "" }
    |
        {$length.value!! > 0}?
        content=bytes[$length.value!!]
        coder=byte
        { isKnownCharset($coder.value!!) }?
        { $value = decodeString($content.value!!, $coder.value!!.toDecoder()!!) }
    );

byteBuffer returns [value: ByteArray]:
    length=int
    content=bytes[$length.value!!]
    { $value = $content.value };

bytes[length: Int] returns [value: ByteArray]:
    { $value = ByteArray($length!!) { (match(Tokens.BYTE) as ByteToken).value.toByte() } };

long returns [value: Long]:
    a=BYTE b=BYTE c=BYTE d=BYTE e=BYTE f=BYTE g=BYTE h=BYTE
    {
        $value = ($a!!.value.toLong() shl 56) or
            ($b!!.value.toLong() shl 48) or
            ($c!!.value.toLong() shl 40) or
            ($d!!.value.toLong() shl 32) or
            ($e!!.value.toLong() shl 24) or
            ($f!!.value.toLong() shl 16) or
            ($g!!.value.toLong() shl 8) or
            $h!!.value.toLong()
    };

int returns [value: Int]:
    a=BYTE b=BYTE c=BYTE d=BYTE
    {
        $value = ($a!!.value.toInt() shl 24) or
            ($b!!.value.toInt() shl 16) or
            ($c!!.value.toInt() shl 8) or
            $d!!.value.toInt()
    };

byte returns [value: Byte]:
    BYTE
    { $value = $BYTE!!.value.toByte() };

boolean returns [value: Boolean]:
    BYTE
    { $BYTE!!.value == 0.toUByte() || $BYTE!!.value == 1.toUByte() }?
    { $value = $BYTE!!.value == 1.toUByte() };