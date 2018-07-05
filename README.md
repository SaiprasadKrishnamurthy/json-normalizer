[![Build Status](https://travis-ci.com/SaiprasadKrishnamurthy/json-normalizer.svg?branch=master)](https://travis-ci.com/SaiprasadKrishnamurthy/json-normalizer)
# JSON Normalizer

The purpose of this utility is to cleanse and normalize the JSON.
* Works for JSON with many null or empty fields and you want to remove them at any level of depth.  
* Works for JSON with many field at different levels with duplicate values (but named differently).
In this case, you want to dedupe the values and inject them into a different field dynamically created.
* Works by means of a declarative approach without writing any code.
* Works for any json with any depth.   

The main motivation behind this utility is to make the JSON nice and compact to be made more usable for NOSQL stores/search engines
like elasticsearch etc where you typically do not want to make your documents noisy.  

## Getting Started

```
<dependency>
    <groupId>com.github.saiprasadkrishnamurthy</groupId>
    <artifactId>json-normalizer</artifactId>
    <version>2.3</version>
</dependency>

// To call from your code.
DocumentSettings documentNormalizationSettings = // Java representation of your config JSON (examples below).
String cleansedJson = Normalizer.normalize(documentNormalizationSettings, inputJson);

```


## Examples

### Example 1
#### Input:
```
{
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith Jim",
  "age": 31,
  "city": "New York"
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "Fields representing all Human Names",
      "sourceFields": [
        "firstName",
        "lastName",
        "fullName"
      ],
      "targetField": "_fullName"
    }
  ]
}
```

#### Output JSON:
```
{
  "fullName": "Jim",
  "age": 31,
  "city": "New York",
  "_fullName": "John Smith"
} 
```

### Example 2
#### Input:
```
{
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "city": "New York"
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "Fields representing all Human identities",
      "sourceFields": [
        "firstName",
        "lastName",
        "fullName",
        "age",
        "city"
      ],
      "targetField": "identity"
    }
  ]
}
```

#### Output JSON:
```
{
  "age": "31",
  "city": "New York",
  "identity": "John Smith"
}
```

### Example 3 (with arrays)
#### Input:
```
{
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "phoneNumbers": ["111", "234", "111"],
  "city": "New York"
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "Fields representing all Human identities",
      "sourceFields": [
        "firstName",
        "lastName",
        "fullName",
        "age",
        "phoneNumbers",
        "city"
      ],
      "targetField": "identity"
    }
  ]
}
```

#### Output JSON:
```
{
  "age": "31",
  "city": "New York",
  "phoneNumbers": [
    "234"
  ],
  "identity": "John Smith 111"
}
```

### Example 4 (automatic removal of nulls)
#### Input:
```
{
  "names": null,
  "coordinates": null,
  "nameDetails": {
    "firstName": null,
    "lastName": "Smith",
    "fullName": "John Smith"
  },
  "age": null,
  "city": "New York",
  "contacts": [
    {
      "homePhone": [
        "111",
        "000",
        "234a"
      ],
      "officePhone": [
        "111",
        "100"
      ],
      "landline": null
    }
  ],
  "addresses": [
    {
      "id": 1,
      "line1": "No 6, Beach Road",
      "line2": "SantaClara Ave",
      "city": null,
      "state": null,
      "country": null
    }
  ]
}
```

#### Settings:
```
{
  "id": "events",
  "description": "clean up empty values"
}
```

#### Output JSON:
```
{
  "nameDetails": {
    "lastName": "Smith",
    "fullName": "John Smith"
  },
  "city": "New York",
  "contacts": [
    {
      "homePhone": [
        "111",
        "000",
        "234a"
      ],
      "officePhone": [
        "111",
        "100"
      ]
    }
  ],
  "addresses": [
    {
      "id": 1,
      "line1": "No 6, Beach Road",
      "line2": "SantaClara Ave"
    }
  ]
}
```

### Example 5 (json with nested structures)
#### Input:
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "phoneNumbers": [
    "111",
    "234",
    "111"
  ],
  "city": "New York",
  "contacts": {
    "telephone": {
      "homePhone": [
        "111",
        "000"
      ],
      "officePhone": [
        "111",
        "100"
      ]
    }
  }
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "names",
      "sourceFields": [
        "firstName",
        "lastName",
        "fullName"
      ],
      "targetField": "_fullName"
    },
    {
      "description": "phones",
      "sourceFields": [
        "phoneNumbers",
        "contacts.telephone.homePhone",
        "contacts.telephone.officePhone"
      ],
      "targetField": "_phoneNumber",
      "fieldsToBeDeleted": [
        "names",
        "coordinates",
        "age",
        "city"
      ]
    }
  ]
}
```

#### Output JSON:
```
{
  "contacts": {
    "telephone": {
      "homePhone": [
        "000"
      ],
      "officePhone": [
        "100"
      ]
    }
  },
  "phoneNumbers": [
    "234"
  ],
  "_fullName": "John Smith",
  "_phoneNumber": "111"
}
```

### Example 6 (another example of multi-level json)
#### Input:
```
{
  "names": null,
  "coordinates": null,
  "firstName": "John",
  "lastName": "Smith",
  "fullName": "John Smith",
  "age": 31,
  "phoneNumber": ["999"],
  "city": "New York",
  "orders": [
    {
      "orderId": "1",
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ],
      "phone": "111"
    },
    {
      "orderId": "2",
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ],
      "phone": "112"
    }
  ],
  "travelTickets": [
    {
      "ticketId": "1",
      "contactPhone": "111"
    }
  ]
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "names",
      "sourceFields": [
        "firstName",
        "lastName",
        "fullName"
      ],
      "targetField": "_fullName"
    },
    {
      "description": "phones",
      "sourceFields": [
        "phoneNumbers",
        "orders.phone",
        "travelTickets.contactPhone"
      ],
      "targetField": "_phoneNumber",
      "fieldsToBeDeleted": [
        "orders.orderId"
      ]
    }
  ]
}
```

#### Output JSON:
```
{
  "age": 31,
  "phoneNumber": [
    "999"
  ],
  "city": "New York",
  "orders": [
    {
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ]
    },
    {
      "total": 100,
      "items": [
        {
          "lineItemId": "1",
          "quantity": 1
        },
        {
          "lineItemId": "2",
          "quantity": 1
        }
      ],
      "phone": "112"
    }
  ],
  "travelTickets": [
    {
      "ticketId": "1"
    }
  ],
  "_fullName": "John Smith",
  "_phoneNumber": "111"
}
```

### Example 7 (removal only)
#### Input:
```
{
  "names": null,
  "coordinates": null,
  "nameDetails": {
    "firstName": "John",
    "lastName": "Smith",
    "fullName": "John Smith"
  },
  "age": 31,
  "city": "New York",
  "contacts": [
    {
      "homePhone": [
        "111",
        "000",
        "234a"
      ],
      "officePhone": [
        "111",
        "100"
      ]
    }
  ]
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "removing contacts",
      "fieldsToBeDeleted": [
        "contacts"
      ]
    }
  ]
}
```

#### Output JSON:
```
{
  "nameDetails": {
    "firstName": "John",
    "lastName": "Smith",
    "fullName": "John Smith"
  },
  "age": 31,
  "city": "New York"
}
```

### Example 8 (multilevel json with removal)
#### Input:
```
{
  "names": null,
  "coordinates": null,
  "nameDetails": {
    "firstName": "John",
    "lastName": "Smith",
    "fullName": "John Smith"
  },
  "age": 31,
  "city": "New York",
  "contacts": [
    {
      "homePhone": [
        "111",
        "000",
        "234a"
      ],
      "officePhone": [
        "111",
        "100"
      ]
    }
  ]
}
```

#### Settings:
```
{
  "id": "events",
  "description": "some description",
  "fieldsGroup": [
    {
      "description": "removing names",
      "fieldsToBeDeleted": [
        "nameDetails.firstName",
        "nameDetails.lastName"
      ]
    },
    {
      "description": "removing contacts",
      "fieldsToBeDeleted": [
        "contacts"
      ]
    }
  ]
}
```

#### Output JSON:
```
{
  "nameDetails": {
    "fullName": "John Smith"
  },
  "age": 31,
  "city": "New York"
}
```
## Authors

* **Sai** - *Initial work* - [Github](https://github.com/SaiprasadKrishnamurthy)


## License

This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details
