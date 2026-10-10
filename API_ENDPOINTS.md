# Defense Logistics API Endpoints

This document provides a comprehensive list of all the services, their internal methods, and the corresponding REST API endpoints with testable `curl` commands.

---

## 1. Unit Service
**Service Methods (`UnitService.java`):**
*   `createUnit(UnitRequest request)`
*   `getUnitById(Long id)`
*   `updateUnit(Long id, UnitRequest request)`
*   `getAllUnits(Pageable pageable)`

**API Endpoints:**

### Create Unit
```bash
curl -X POST http://localhost:8080/api/v1/units \
-H "Content-Type: application/json" \
-d '{
  "name": "Alpha Company",
  "code": "ALPHA-01",
  "location": "Base 1",
  "status": "ACTIVE"
}'
```

### Get Unit by ID
```bash
curl -X GET http://localhost:8080/api/v1/units/1
```

### Update Unit
```bash
curl -X PUT http://localhost:8080/api/v1/units/1 \
-H "Content-Type: application/json" \
-d '{
  "name": "Alpha Company",
  "code": "ALPHA-01",
  "location": "Forward Operating Base 2",
  "status": "DEPLOYED"
}'
```

### List Units (Paginated)
```bash
curl -X GET "http://localhost:8080/api/v1/units?page=0&size=10"
```

---

## 2. Personnel Service
**Service Methods (`PersonnelService.java`):**
*   `createPersonnel(PersonnelRequest request)`
*   `getPersonnelById(Long id)`
*   `updatePersonnel(Long id, PersonnelRequest request)`
*   `getAllPersonnel(Pageable pageable)`

**API Endpoints:**

### Create Personnel
*(Note: Requires a valid `unitId` from the Unit Service)*
```bash
curl -X POST http://localhost:8080/api/v1/personnels \
-H "Content-Type: application/json" \
-d '{
  "serviceNumber": "SN-10045",
  "fullName": "Jane Doe",
  "role": "Driver",
  "unitId": 1,
  "posting": "Transport Division",
  "availabilityStatus": "AVAILABLE",
  "contactDetails": "jane.doe@defense.gov"
}'
```

### Get Personnel by ID
```bash
curl -X GET http://localhost:8080/api/v1/personnels/1
```

### Update Personnel
```bash
curl -X PUT http://localhost:8080/api/v1/personnels/1 \
-H "Content-Type: application/json" \
-d '{
  "serviceNumber": "SN-10045",
  "fullName": "Jane Doe",
  "role": "Lead Driver",
  "unitId": 1,
  "posting": "Transport Division",
  "availabilityStatus": "DEPLOYED",
  "contactDetails": "jane.doe@defense.gov"
}'
```

### List Personnel (Paginated)
```bash
curl -X GET "http://localhost:8080/api/v1/personnels?page=0&size=10"
```

---

## 3. Asset Service
**Service Methods (`AssetService.java`):**
*   `createAsset(AssetRequest request)`
*   `getAssetById(Long id)`
*   `updateAsset(Long id, AssetRequest request)`
*   `getAllAssets(Pageable pageable)`

**API Endpoints:**

### Create Asset
*(Note: Requires a valid `unitId` from the Unit Service)*
```bash
curl -X POST http://localhost:8080/api/v1/assets \
-H "Content-Type: application/json" \
-d '{
  "assetCode": "COMM-Radio-09",
  "name": "Encrypted Field Radio",
  "category": "COMM",
  "unitId": 1,
  "location": "Armory B",
  "operationalStatus": "OPERATIONAL",
  "condition": "NEW"
}'
```

### Get Asset by ID
```bash
curl -X GET http://localhost:8080/api/v1/assets/1
```

### Update Asset
```bash
curl -X PUT http://localhost:8080/api/v1/assets/1 \
-H "Content-Type: application/json" \
-d '{
  "assetCode": "COMM-Radio-09",
  "name": "Encrypted Field Radio",
  "category": "COMM",
  "unitId": 1,
  "location": "Maintenance Bay",
  "operationalStatus": "IN_MAINTENANCE",
  "condition": "FAIR"
}'
```

### List Assets (Paginated)
```bash
curl -X GET "http://localhost:8080/api/v1/assets?page=0&size=10"
```

---

## 4. Vehicle Service
**Service Methods (`VehicleService.java`):**
*   `createVehicle(VehicleRequest request)`
*   `getVehicleById(Long id)`
*   `updateVehicle(Long id, VehicleRequest request)`
*   `getAllVehicles(Pageable pageable)`

**API Endpoints:**

### Create Vehicle (Without Driver)
*(Note: Requires a valid `unitId`)*
```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
-H "Content-Type: application/json" \
-d '{
  "registrationNumber": "TRK-7742",
  "vehicleType": "TRUCK",
  "unitId": 1,
  "location": "Motor Pool Alpha",
  "operationalStatus": "OPERATIONAL",
  "condition": "GOOD"
}'
```

### Create / Update Vehicle (Assigning a Driver)
*(Note: Requires a valid `driverPersonnelId` and the driver's availabilityStatus must be `AVAILABLE`)*
```bash
curl -X PUT http://localhost:8080/api/v1/vehicles/1 \
-H "Content-Type: application/json" \
-d '{
  "registrationNumber": "TRK-7742",
  "vehicleType": "TRUCK",
  "unitId": 1,
  "location": "Motor Pool Alpha",
  "operationalStatus": "OPERATIONAL",
  "condition": "GOOD",
  "driverPersonnelId": 1
}'
```

### Get Vehicle by ID
```bash
curl -X GET http://localhost:8080/api/v1/vehicles/1
```

### List Vehicles (Paginated)
```bash
curl -X GET "http://localhost:8080/api/v1/vehicles?page=0&size=10"
```
