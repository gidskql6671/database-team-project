const now = new Date();

function getDate(date) {
  return date.toLocaleDateString().replace(/\./g, '').replace(/\s/g, '-');
}

function getTime(date) {
  return date.toTimeString().slice(0, 8);
}

document.getElementById('reservedDate').value = getDate(now);
document.getElementById('startTime').value = getTime(now);
document.getElementById('endTime').value = getTime(now);

async function reserve() {
  const form = document.getElementById("form_reservation");

  const buildingNumber = form.buildingNumber.value;
  const roomCode = form.roomCode.value;
  const startDateTime = form.startDateTime.value;
  const endDateTime = form.endDateTime.value;

  const response = await fetch("http://localhost:8080/api/classroom", {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      buildingNumber: buildingNumber,
      roomCode: roomCode,
      startDateTime: startDateTime,
      endDateTime: endDateTime
    })
  });

  if (response.ok) {
    location.reload();
  }
  else {
    const reason = (await response.json()).msg

    alert(`강의실 예약이 실패했습니다.\n사유: ${reason}`);
  }

  return false;
}

async function searchRoom() {
  const timeSlots = document.getElementById('timeSlots');
  const form = document.getElementById('form_reservation');

  const searchDate = form.date.value;
  const buildingNumber = form.buildingNumber.value;
  const roomCode = form.roomCode.value;

  const url = new URL("http://localhost:8080/api/classroom/available");
  url.searchParams.append('buildingNumber', buildingNumber);
  url.searchParams.append('roomCode', roomCode);
  url.searchParams.append('date', searchDate);

  const response = await fetch(url, {method: 'GET'});

  // TODO 예외 처리

  const resData = await response.json();

  const availableTimes = resData
      .flatMap(([start, end]) => [...Array(end - start).keys()].map(key => key + start))

  timeSlots.innerHTML = '';
  for (let i = 7; i <= 23; i++) {
    const timeSlot = document.createElement('div');
    timeSlot.classList.add('timeSlot');

    if (availableTimes.includes(i)) {
      timeSlot.classList.add('available');
    }

    const hour = document.createElement('div');
    hour.classList.add('hour');
    hour.textContent = i.toString();
    timeSlot.appendChild(hour);

    timeSlots.appendChild(timeSlot);
  }
}

function reserveRoom() {
  // 예약 처리 구현
}