import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { API_BASE_URL } from '../config/apiConfig';

const API_URL = API_BASE_URL;

const getAuthHeaders = async () => {
  const token = await AsyncStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

export const personalizationService = {
  // Get personalized feed
  getPersonalizedFeed: async (limit = 20) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/personalization/feed?limit=${limit}`, { headers });
    return response.data;
  },

  // Track user interaction
  trackInteraction: async (eventId, type) => {
    const headers = await getAuthHeaders();
    await axios.post(
      `${API_URL}/personalization/track`,
      { eventId, type },
      { headers }
    );
  },

  // Check onboarding status
  checkOnboardingStatus: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/personalization/onboarding/status`, { headers });
    return response.data;
  },

  // Complete onboarding
  completeOnboarding: async (categoryIds) => {
    const headers = await getAuthHeaders();
    await axios.post(
      `${API_URL}/personalization/onboarding/complete`,
      { categoryIds },
      { headers }
    );
  },
};

export const socialService = {
  // Get friends list
  getFriends: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/social/friends`, { headers });
    return response.data;
  },

  // Follow user
  followUser: async (userId) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/social/friends/${userId}/follow`, {}, { headers });
  },

  // Unfollow user
  unfollowUser: async (userId) => {
    const headers = await getAuthHeaders();
    await axios.delete(`${API_URL}/social/friends/${userId}/unfollow`, { headers });
  },

  // Get friend activity feed
  getActivityFeed: async (limit = 50) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/social/activity/feed?limit=${limit}`, { headers });
    return response.data;
  },

  // Get friends attending event
  getFriendsAttending: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/social/events/${eventId}/attendees`, { headers });
    return response.data;
  },

  // Send chat message
  sendChatMessage: async (eventId, message, replyToId = null) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(
      `${API_URL}/social/events/${eventId}/chat`,
      { message, replyToId },
      { headers }
    );
    return response.data;
  },

  // Get chat messages
  getChatMessages: async (eventId, since = null) => {
    const headers = await getAuthHeaders();
    const url = since 
      ? `${API_URL}/social/events/${eventId}/chat?since=${since}`
      : `${API_URL}/social/events/${eventId}/chat`;
    const response = await axios.get(url, { headers });
    return response.data;
  },

  // Create invite
  createInvite: async (eventId, inviteeId = null, email = null, phone = null) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(
      `${API_URL}/social/invites/create`,
      { eventId, inviteeId, email, phone },
      { headers }
    );
    return response.data;
  },

  // Accept invite
  acceptInvite: async (token) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(`${API_URL}/social/invites/${token}/accept`, {}, { headers });
    return response.data;
  },
};

export const filterService = {
  // Filter events
  filterEvents: async (filters) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(`${API_URL}/filters/events`, filters, { headers });
    return response.data;
  },

  // Save filter preset
  savePreset: async (name, filter) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(`${API_URL}/filters/presets`, { name, filter }, { headers });
    return response.data;
  },

  // Get user presets
  getPresets: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/filters/presets`, { headers });
    return response.data;
  },

  // Delete preset
  deletePreset: async (presetId) => {
    const headers = await getAuthHeaders();
    await axios.delete(`${API_URL}/filters/presets/${presetId}`, { headers });
  },

  // Apply preset
  applyPreset: async (presetId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/filters/presets/${presetId}/apply`, { headers });
    return response.data;
  },
};

export const notificationService = {
  // Register device token
  registerToken: async (token, deviceType) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/notifications/register`, { token, deviceType }, { headers });
  },

  // Update preferences
  updatePreferences: async (preferences) => {
    const headers = await getAuthHeaders();
    await axios.put(`${API_URL}/notifications/preferences`, preferences, { headers });
  },

  // Get preferences
  getPreferences: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/notifications/preferences`, { headers });
    return response.data;
  },

  // Get notification history
  getHistory: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/notifications/history`, { headers });
    return response.data;
  },
};

export const analyticsService = {
  // Get analytics overview
  getOverview: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/overview`, { headers });
    return response.data;
  },

  // Get sales funnel
  getFunnel: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/funnel`, { headers });
    return response.data;
  },

  // Get traffic sources
  getTraffic: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/traffic`, { headers });
    return response.data;
  },

  // Get demographics
  getDemographics: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/demographics`, { headers });
    return response.data;
  },

  // Get revenue
  getRevenue: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/revenue`, { headers });
    return response.data;
  },

  // Export CSV
  exportCSV: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/export`, { headers });
    return response.data;
  },

  // Track view
  trackView: async (eventId, source = 'DIRECT_LINK') => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/analytics/events/${eventId}/track/view?source=${source}`, {}, { headers });
  },
};

export const ticketService = {
  // Get user tickets
  getMyTickets: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/tickets/my-tickets`, { headers });
    return response.data;
  },

  // Get ticket details
  getTicket: async (ticketId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/tickets/${ticketId}`, { headers });
    return response.data;
  },

  // Mark as downloaded
  markAsDownloaded: async (ticketId) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/tickets/${ticketId}/download`, {}, { headers });
  },

  // Get Apple Wallet pass
  getAppleWalletPass: async (ticketId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/tickets/${ticketId}/wallet/apple`, { headers });
    return response.data;
  },

  // Get Google Wallet pass
  getGoogleWalletPass: async (ticketId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/tickets/${ticketId}/wallet/google`, { headers });
    return response.data;
  },

  // Transfer ticket
  transferTicket: async (ticketId, recipientId) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/tickets/${ticketId}/transfer`, { recipientId }, { headers });
  },
};

// PRIORITY 2 SERVICES

export const reviewService = {
  // Create review
  createReview: async (eventId, rating, comment, vibeTags) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(
      `${API_URL}/reviews`,
      { eventId, rating, comment, vibeTags },
      { headers }
    );
    return response.data;
  },

  // Get event reviews
  getEventReviews: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/reviews/events/${eventId}`, { headers });
    return response.data;
  },

  // Get verified reviews
  getVerifiedReviews: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/reviews/events/${eventId}/verified`, { headers });
    return response.data;
  },

  // Get average rating
  getAverageRating: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/reviews/events/${eventId}/rating`, { headers });
    return response.data;
  },

  // Mark helpful
  markHelpful: async (reviewId) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/reviews/${reviewId}/helpful`, {}, { headers });
  },
};

export const loyaltyService = {
  // Get user tier
  getUserTier: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/loyalty/tier`, { headers });
    return response.data;
  },

  // Get points history
  getPointsHistory: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/loyalty/points/history`, { headers });
    return response.data;
  },

  // Redeem points
  redeemPoints: async (points) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/loyalty/points/redeem`, { points }, { headers });
  },
};

export const checkInService = {
  // Check in to event
  checkIn: async (ticketId) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(`${API_URL}/checkin/tickets/${ticketId}`, {}, { headers });
    return response.data;
  },

  // Get user check-ins
  getMyCheckIns: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/checkin/my-checkins`, { headers });
    return response.data;
  },

  // Get check-in by ticket
  getCheckInByTicket: async (ticketId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/checkin/tickets/${ticketId}`, { headers });
    return response.data;
  },
};

export const memoryWallService = {
  // Upload memory
  uploadMemory: async (eventId, photoUrl, caption) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(
      `${API_URL}/memories`,
      { eventId, photoUrl, caption },
      { headers }
    );
    return response.data;
  },

  // Get event memories
  getEventMemories: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/memories/events/${eventId}`, { headers });
    return response.data;
  },

  // Get my memories
  getMyMemories: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/memories/my-memories`, { headers });
    return response.data;
  },

  // Like memory
  likeMemory: async (memoryId) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/memories/${memoryId}/like`, {}, { headers });
  },
};

export const happeningNowService = {
  // Get happening now events
  getHappeningNow: async (lat, lon) => {
    const params = lat && lon ? `?lat=${lat}&lon=${lon}` : '';
    const response = await axios.get(`${API_URL}/happening-now${params}`);
    return response.data;
  },
};

export const dynamicPricingService = {
  // Get current price
  getCurrentPrice: async (eventId) => {
    const response = await axios.get(`${API_URL}/pricing/events/${eventId}/current`);
    return response.data;
  },

  // Get price history
  getPriceHistory: async (eventId) => {
    const response = await axios.get(`${API_URL}/pricing/events/${eventId}/history`);
    return response.data;
  },
};

export const discountCodeService = {
  // Validate code
  validateCode: async (code, eventId) => {
    const response = await axios.get(`${API_URL}/discount-codes/validate?code=${code}&eventId=${eventId}`);
    return response.data;
  },

  // Apply code
  applyCode: async (code, eventId) => {
    await axios.post(`${API_URL}/discount-codes/apply`, { code, eventId });
  },

  // Get event codes
  getEventCodes: async (eventId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/discount-codes/events/${eventId}`, { headers });
    return response.data;
  },
};

export const groupTicketService = {
  // Create group ticket
  createGroupTicket: async (eventId, totalTickets, participantUserIds) => {
    const headers = await getAuthHeaders();
    const response = await axios.post(
      `${API_URL}/group-tickets`,
      { eventId, totalTickets, participantUserIds },
      { headers }
    );
    return response.data;
  },

  // Get my group tickets
  getMyGroupTickets: async () => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/group-tickets/my-groups`, { headers });
    return response.data;
  },

  // Get group ticket
  getGroupTicket: async (groupTicketId) => {
    const headers = await getAuthHeaders();
    const response = await axios.get(`${API_URL}/group-tickets/${groupTicketId}`, { headers });
    return response.data;
  },

  // Pay for ticket
  payForTicket: async (participantId) => {
    const headers = await getAuthHeaders();
    await axios.post(`${API_URL}/group-tickets/participants/${participantId}/pay`, {}, { headers });
  },
};

export const bundleService = {
  // Get event bundles
  getEventBundles: async (eventId) => {
    const response = await axios.get(`${API_URL}/bundles/events/${eventId}`);
    return response.data;
  },

  // Get restaurants
  getRestaurants: async (eventId) => {
    const response = await axios.get(`${API_URL}/bundles/events/${eventId}/restaurants`);
    return response.data;
  },

  // Get transport
  getTransport: async (eventId) => {
    const response = await axios.get(`${API_URL}/bundles/events/${eventId}/transport`);
    return response.data;
  },

  // Get alternatives
  getAlternatives: async (eventId) => {
    const response = await axios.get(`${API_URL}/bundles/events/${eventId}/alternatives`);
    return response.data;
  },
};
