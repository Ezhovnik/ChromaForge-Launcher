package chromaforge.launcher.data.dv;

public sealed interface dvValue
    permits dvNull, dvBool, dvLong, dvDouble, dvString, dvArray, dvObject  {}
