grammar GatlingSimulationLog;

@header {
    import de.joshuagleitze.gatling.simulationlog.parser.tokens.ByteToken
}

runRecord:
    gatlingVersion=string
    simulationClassName=string
    startEpochMillis=long
    ;

string locals [bytesRead: Int = 0]:
    length=int
    bytes=byteSequence[$length.value]
    coder=byte
    ;

long returns [value: Long]:
    a=BYTE b=BYTE c=BYTE d=BYTE e=BYTE f=BYTE g=BYTE h=BYTE
    {
        $value = (($a as ByteToken).value.toLong() shl 56) or
            (($b as ByteToken).value.toLong() shl 48) or
            (($c as ByteToken).value.toLong() shl 40) or
            (($d as ByteToken).value.toLong() shl 32) or
            (($e as ByteToken).value.toLong() shl 24) or
            (($f as ByteToken).value.toLong() shl 16) or
            (($g as ByteToken).value.toLong() shl 8) or
            ($h as ByteToken).value.toLong()
    };

int returns [value: Int]:
    a=BYTE b=BYTE c=BYTE d=BYTE
    {
        $value = (($a as ByteToken).value.toInt() shl 24) or
            (($b as ByteToken).value.toInt() shl 16) or
            (($c as ByteToken).value.toInt() shl 8) or
            ($d as ByteToken).value.toInt()
    };

byteSequence[count: Int] returns [value: ByteArray] locals [remaining: Int]:
    {
        $value = ByteArray(count)
        $remaining = $count
    }
    (
        {$remaining > 0}?
        BYTE
        {
            $value += ($BYTE as ByteToken).value.toByte()
            $remaining -= 1
        }
    )*;

byte returns [value: Byte]:
    BYTE
    {
        $value = ($BYTE as ByteToken).value.toByte()
    };

BYTE: '\u0000'..'\u00FF';