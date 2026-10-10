import urllib.request, urllib.parse, json

headers = {"Authorization": "Bearer admin", "Content-Type": "application/json"}
def post(url, data):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), headers=headers, method='POST')
    try: urllib.request.urlopen(req)
    except Exception as e: print(f"Error seeding {url}: {e}")

# Seed Resource Data
post("http://localhost:9191/api/v1/units", {"name": "Omega Squadron", "code": "OM-01", "location": "Base Alpha", "status": "ACTIVE"})
post("http://localhost:9191/api/v1/units", {"name": "Delta Force", "code": "DF-09", "location": "Outpost Bravo", "status": "ACTIVE"})
post("http://localhost:9191/api/v1/personnels", {"serviceNumber": "SN-001", "fullName": "John Doe", "role": "Commander", "unitId": 1, "posting": "HQ", "availabilityStatus": "AVAILABLE"})
post("http://localhost:9191/api/v1/personnels", {"serviceNumber": "SN-002", "fullName": "Jane Smith", "role": "Medic", "unitId": 2, "posting": "Field", "availabilityStatus": "DEPLOYED"})
post("http://localhost:9191/api/v1/assets", {"assetCode": "MED-01", "category": "MEDICAL", "location": "Base Alpha", "condition": "EXCELLENT", "operationalStatus": "AVAILABLE"})
post("http://localhost:9191/api/v1/vehicles", {"registrationNumber": "TANK-01", "vehicleType": "ARMORED", "location": "Base Alpha", "condition": "GOOD", "operationalStatus": "AVAILABLE"})

# Seed Inventory Data
post("http://localhost:9192/api/v1/supplies", {"itemCode": "AMMO-556", "name": "5.56mm Rounds", "category": "WEAPON", "unitOfMeasure": "CRATE", "description": "Standard", "reorderLevel": 50})
post("http://localhost:9192/api/v1/supplies", {"itemCode": "MRE-01", "name": "MRE Rations", "category": "FOOD", "unitOfMeasure": "BOX", "description": "Meals", "reorderLevel": 100})
post("http://localhost:9192/api/v1/inventory/receive", {"supplyItemId": 1, "location": "Base Alpha", "quantity": 5000, "reference": "PO-100"})
post("http://localhost:9192/api/v1/inventory/receive", {"supplyItemId": 2, "location": "Base Alpha", "quantity": 200, "reference": "PO-101"})

# Seed Requests
post("http://localhost:9193/api/v1/requests", {"requestNumber": "REQ-001", "requestingUnitId": 1, "requestedBy": "johndoe", "purpose": "Training", "priority": "HIGH", "deliveryLocation": "Base Alpha", "status": "PENDING", "items": [{"supplyItemId": 1, "quantityRequested": 500}]})
print("Database seeded!")
