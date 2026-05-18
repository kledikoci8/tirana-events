import api from './api';

export async function trackEventView(eventId) {
  try {
    await api.post('/personalization/track', {
      eventId,
      type: 'VIEW',
    });
  } catch {
    // Non-blocking
  }
}

export async function trackEventSave(eventId) {
  try {
    await api.post('/personalization/track', {
      eventId,
      type: 'SAVE',
    });
  } catch {
    // Non-blocking
  }
}
