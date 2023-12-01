const now = new Date();

function getDate(date) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const dateNum = date.getDate();

  return `${year}-${month >= 10 ? month : '0' + month}-${dateNum >= 10 ? dateNum : '0' + dateNum}`;
}

document.getElementById('reservedDate').value = getDate(now);

const urlParams = new URLSearchParams(window.location.search);
document.getElementById('form_search').buildingNumber.value = urlParams.get('buildingNumber');

function setTimeInput() {
  const form = document.getElementById("form_reservation");

  const startTime = form.startTime;
  const endTime = form.endTime;

  for (let i = 8; i <= 21; i++) {
    const opt = document.createElement("option");
    opt.value = i.toString();
    opt.text = `${i}시`;
    startTime.appendChild(opt);
  }

  for (let i = 9; i <= 22; i++) {
    const opt = document.createElement("option");
    opt.value = i.toString();
    opt.text = `${i}시`;
    endTime.appendChild(opt);
  }
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

  const response = await fetch(url, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' }
  });

  if (response.status === 404) {
    alert("존재하지 않는 강의실입니다.");

    return;
  }

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

async function reserveRoom() {
  const form = document.getElementById("form_reservation");

  const reservedDate = form.date.value;
  const buildingNumber = form.buildingNumber.value;
  const roomCode = form.roomCode.value;
  const startTime = Number(form.startTime.value);
  const endTime = Number(form.endTime.value);

  if (startTime >= endTime) {
    alert("예약 시간이 부적합합니다.");

    return false;
  }

  const startDateTime = new Date(`${reservedDate} ${startTime}:00:00`);
  const endDateTime = new Date(`${reservedDate} ${endTime}:00:00`);

  startDateTime.setHours(startDateTime.getHours() + 9);
  endDateTime.setHours(endDateTime.getHours() + 9);

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

async function cancelReservation(reservedId) {
  const response = await fetch("http://localhost:8080/api/classroom", {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({reservedId: reservedId})
  });

  if (response.ok) {
    location.reload();
  }
  else {
    const reason = (await response.json()).msg

    alert(`강의실 예약 취소가 실패했습니다.\n사유: ${reason}`);
  }

  return false;
}

function setupReservedList() {
  const elements = document.querySelectorAll(".reservedClass");

  elements.forEach(element => {
    const startDatetime = new Date(element.dataset.startDt);
    const endDatetime = new Date(element.dataset.endDt);
    const date = startDatetime.toLocaleDateString();
    const startHour = startDatetime.getHours();
    const endHour = endDatetime.getHours();

    const datetimeEle = element.querySelector(".datetime")

    datetimeEle.innerText = `${date} ${startHour}시 ~ ${endHour}시`;

    if (startDatetime <= now) {
      element.querySelector(".cancelBtn").style.visibility = "hidden";
    }
  })
}

setTimeInput();
setupReservedList();