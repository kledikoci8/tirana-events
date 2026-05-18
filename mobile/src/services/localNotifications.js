import * as Notifications from 'expo-notifications';
import { Platform } from 'react-native';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: true,
  }),
});

export async function ensureNotificationPermissions() {
  const { status: existing } = await Notifications.getPermissionsAsync();
  if (existing === 'granted') return true;
  const { status } = await Notifications.requestPermissionsAsync();
  return status === 'granted';
}

/** Schedule 24h and 2h reminders for an upcoming ticket (no Firebase — local only). */
export async function scheduleTicketReminders(ticket) {
  const granted = await ensureNotificationPermissions();
  if (!granted) return;

  const eventDate = ticket.eventDate || ticket.event?.startDate;
  if (!eventDate) return;

  const start = new Date(eventDate);
  const now = Date.now();
  const eventName = ticket.eventName || ticket.event?.name || 'your event';

  const schedules = [
    { hours: 24, title: 'Event tomorrow', body: `${eventName} starts tomorrow` },
    { hours: 2, title: 'Starting soon', body: `${eventName} starts in 2 hours` },
  ];

  for (const s of schedules) {
    const triggerMs = start.getTime() - s.hours * 60 * 60 * 1000;
    if (triggerMs <= now) continue;

    await Notifications.scheduleNotificationAsync({
      content: {
        title: s.title,
        body: s.body,
        data: { eventId: ticket.eventId || ticket.event?.id, ticketId: ticket.id },
      },
      trigger: { date: new Date(triggerMs) },
    });
  }
}

export async function scheduleAllTicketReminders(tickets) {
  for (const ticket of tickets) {
    try {
      await scheduleTicketReminders(ticket);
    } catch (e) {
      console.warn('Failed to schedule reminder', e);
    }
  }
}
