# excel-to-json
Command line utility to convert an excel file to a specific json or yaml format. 

## usage
java -jar excel-to-service.jar -s sourcefile [options...]

-s src/main/resources/sysint.xlsx -pretty -fr 2 -lr 13 -nc 1 -lc 2 -pc 3 -vc 4 -f yaml

```
 -?,--help <arg>           This help text.
 -d,--destination <arg>    The destination directory where the service.json/service.yaml should be created.
 -f,--format <arg>         The format into which the file should be converted [JSON, YAML].                           
 -fr,--firstRow <arg>      The first row to read
 -lc,--lengthCol <arg>     The column that contains the field length
 -lr,--lastRow <arg>       The last row to read
 -nc,--nameCol <arg>       The column that contains the field name
 -pc,--positionCol <arg>   The column that contains the field position
 -pretty                   To render output as pretty formatted json/yaml.
 -s,--source <arg>         The source file which should be converted.                       
 -vc,--valueCol <arg>      The column that contains the default field value.
                       
```

## input Excel (voorbeeld)

| Name | Length  | Position  | Value  |
| :----- | -----: | -----: | -----: |
| eventType | 1 | 1 | M |
| eventTimestamp | 14 | 2 | |
| eventStatus | 3 | 16 | STA |
| hasContract | 7 | 19 | |
| hasMutType | 2 | 26 | MT |
| resultStatus | 2 | 28 | |
| foutkode1 | 4 | 30 | |
| foutoms1 | 100 | 34 | |
| fouttype1 | 1 | 134 | |
| id | 36 | 135 | |
| received | 24 | 171 | |
| filler | 105 | 195 | FILLER |

## output YAML

java -jar excel-to-service.jar 
-s src/main/resources/sysint.xlsx 
-pretty 
-fr 2 
-lr 13 
-nc 1 
-lc 2 
-pc 3 
-vc 4 
-f yaml

```
---
HASREQ:
  type: object
  properties:
    eventType:
      maxLength: 1
      value: M
    eventTimestamp:
      maxLength: 14
      empty: true
    eventStatus:
      maxLength: 3
      value: STA
    hasContract:
      maxLength: 7
      empty: true
    hasMutType:
      maxLength: 2
      value: MT
    resultStatus:
      maxLength: 2
      empty: true
    foutkode1:
      maxLength: 4
      empty: true
    foutoms1:
      maxLength: 100
      empty: true
    fouttype1:
      maxLength: 1
      empty: true
    id:
      maxLength: 36
      empty: true
    received:
      maxLength: 24
      empty: true
    filler:
      maxLength: 105
      value: FILLER
HASRES:
  type: object
  resDataStatus:
    from: 28
    maxLength: 2
    ok:
    - 00
  errors:
  - fout_code:
      from: 30
      maxLength: 4
      pattern: "[a-zA-Z0-9]"
    fout_oms:
      from: 34
      maxLength: 100
      pattern: "[a-zA-Z0-9]"
    fout_type:
      from: 134
      maxLength: 1
      pattern: "[a-zA-Z0-9]"
  data:
    id:
      from: 135
      maxLength: 36
    received:
      from: 171
      maxLength: 24
    filler:
      from: 195
      maxLength: 105

```

## output json

java -jar excel-to-service.jar
-s src/main/resources/sysint.xlsx
-pretty
-fr 2
-lr 13
-nc 1
-lc 2
-pc 3
-vc 4
-f json

```
{
  "HASREQ" : {
    "type" : "object",
    "properties" : {
      "eventType" : {
        "maxLength" : 1,
        "value" : "M"
      },
      "eventTimestamp" : {
        "maxLength" : 14,
        "empty" : true
      },
      "eventStatus" : {
        "maxLength" : 3,
        "value" : "STA"
      },
      "hasContract" : {
        "maxLength" : 7,
        "empty" : true
      },
      "hasMutType" : {
        "maxLength" : 2,
        "value" : "MT"
      },
      "resultStatus" : {
        "maxLength" : 2,
        "empty" : true
      },
      "foutkode1" : {
        "maxLength" : 4,
        "empty" : true
      },
      "foutoms1" : {
        "maxLength" : 100,
        "empty" : true
      },
      "fouttype1" : {
        "maxLength" : 1,
        "empty" : true
      },
      "id" : {
        "maxLength" : 36,
        "empty" : true
      },
      "received" : {
        "maxLength" : 24,
        "empty" : true
      },
      "filler" : {
        "maxLength" : 105,
        "value" : "FILLER"
      }
    }
  },
  "HASRES" : {
    "type" : "object",
    "resDataStatus" : {
      "from" : 28,
      "maxLength" : 2,
      "ok" : [ "00" ]
    },
    "errors" : [ {
      "fout_code" : {
        "from" : 30,
        "maxLength" : 4,
        "pattern" : "[a-zA-Z0-9]"
      },
      "fout_oms" : {
        "from" : 34,
        "maxLength" : 100,
        "pattern" : "[a-zA-Z0-9]"
      },
      "fout_type" : {
        "from" : 134,
        "maxLength" : 1,
        "pattern" : "[a-zA-Z0-9]"
      }
    } ],
    "data" : {
      "id" : {
        "from" : 135,
        "maxLength" : 36
      },
      "received" : {
        "from" : 171,
        "maxLength" : 24
      },
      "filler" : {
        "from" : 195,
        "maxLength" : 105
      }
    }
  }
}

```