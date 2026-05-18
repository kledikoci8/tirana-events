import AsyncStorage from '@react-native-async-storage/async-storage';

const CACHE_KEY = '@tirana_offline_tickets';

export async function cacheTickets(tickets) {
  if (!tickets?.length) return;
  await AsyncStorage.setItem(CACHE_KEY, JSON.stringify(tickets));
}

export async function getCachedTickets() {
  try {
    const raw = await AsyncStorage.getItem(CACHE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function mergeWithCache(liveTickets, cachedTickets) {
  if (liveTickets?.length) return liveTickets;
  return cachedTickets;
}
