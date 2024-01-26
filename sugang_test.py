import random

import requests
import time
import threading

f = open("users.txt", "r")
user_ids = [user_id.strip() for user_id in f.readlines()]
f.close()

sessions = []
for user_id in user_ids:
    s = requests.Session()
    sessions.append(s)

    data = {
        "loginId": user_id,
        "loginPassword": user_id
    }
    res = s.post("http://localhost:8080/api/users/login", json=data)

def sugang(session, num):
    session: requests.Session = session
    data = {
        "lectureCode": "CLTR0090",
        "sectionCode": "1"
    }
    res = session.post("http://localhost:8080/api/sugang", json=data)

    if res.status_code == 200:
        print(f"{num + 1}번째 스레드 성공")
    else:
        print(f"{num + 1}번째 스레드 실패 : {res.text}")


threads = []
for i, s in enumerate(sessions):
    threads.append(threading.Thread(target=sugang, args=[s, i]))

for t in threads:
    t.start()

for t in threads:
    t.join()

for i, s in enumerate(sessions):
    s: requests.Session = s
    data = {
        "lectureCode": "CLTR0090",
        "sectionCode": "1"
    }
    res = s.delete("http://localhost:8080/api/sugang", json=data)

    # print(f"{i}번째 - {res.status_code}, {res.text}")

for s in sessions:
    s.close()