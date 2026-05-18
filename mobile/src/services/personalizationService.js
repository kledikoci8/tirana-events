import axios from '../config/axiosConfig';
import config from '../config/api';

const API_URL = config.apiUrl;

export const personalizationService = {
  // Get personalized feed
  getPersonalizedFeed: async (limit = 20) => {
    const response = await axios.get(`${API_URL}/personalization/feed?limit=${limit}`);
    return response.data;
  },

  // Track user interaction
  trackInteraction: async (eventId, type) => {
    await axios.post(`${API_URL}/personalization/track`, { eventId, type });
  },

  // Check onboarding status
  checkOnboardingStatus: async () => {
    const response = await axios.get(`${API_URL}/personalization/onboarding/status`);
    return response.data;
  },

  // Complete onboarding
  completeOnboarding: async (categoryIds) => {
    await axios.post(`${API_URL}/personalization/onboarding/complete`, { categoryIds });
  },
};

export const socialService = {
  // Get friends list
  getFriends: async () => {
    const response = await axios.get(`${API_URL}/social/friends`);
    return response.data;
  },

  // Follow user
  followUser: async (userId) => {
    await axios.post(`${API_URL}/social/friends/${userId}/follow`);
  },

  // Unfollow user
  unfollowUser: async (userId) => {
    await axios.delete(`${API_URL}/social/friends/${userId}/unfollow`);
  },

  // Get friend activity feed
  getActivityFeed: async (limit = 50) => {
    const response = await axios.get(`${API_URL}/social/activity/feed?limit=${limit}`);
    return response.data;
  },

  // Get friends attending event
  getFriendsAttending: async (eventId) => {
    const response = await axios.get(`${API_URL}/social/events/${eventId}/attendees`);
    return response.data;
  },

  // Get friends attending multiple events (BATCH)
  getBatchFriendsAttending: async (eventIds) => {
    const response = await axios.post(`${API_URL}/social/events/batch-attendees`, eventIds);
    return response.data;
  },

  // Send chat message
  sendChatMessage: async (eventId, message, replyToId = null) => {
    const response = await axios.post(`${API_URL}/social/events/${eventId}/chat`, { message, replyToId });
    return response.data;
  },

  // Get chat messages
  getChatMessages: async (eventId, since = null) => {
    const url = since 
      ? `${API_URL}/social/events/${eventId}/chat?since=${since}`
      : `${API_URL}/social/events/${eventId}/chat`;
    const response = await axios.get(url);
    return response.data;
  },

  // Create invite
  createInvite: async (eventId, inviteeId = null, email = null, phone = null) => {
    const response = await axios.post(`${API_URL}/social/invites/create`, { eventId, inviteeId, email, phone });
    return response.data;
  },

  // Accept invite
  acceptInvite: async (token) => {
    const response = await axios.post(`${API_URL}/social/invites/${token}/accept`);
    return response.data;
  },
};

export const filterService = {
  // Filter events
  filterEvents: async (filters) => {
    const response = await axios.post(`${API_URL}/filters/events`, filters);
    return response.data;
  },

  // Save filter preset
  savePreset: async (name, filter) => {
    const response = await axios.post(`${API_URL}/filters/presets`, { name, filter });
    return response.data;
  },

  // Get user presets
  getPresets: async () => {
    const response = await axios.get(`${API_URL}/filters/presets`);
    return response.data;
  },

  // Delete preset
  deletePreset: async (presetId) => {
    await axios.delete(`${API_URL}/filters/presets/${presetId}`);
  },

  // Apply preset
  applyPreset: async (presetId) => {
    const response = await axios.get(`${API_URL}/filters/presets/${presetId}/apply`);
    return response.data;
  },
};

export const notificationService = {
  // Register device token
  registerToken: async (token, deviceType) => {
    await axios.post(`${API_URL}/notifications/register`, { token, deviceType });
  },

  // Update preferences
  updatePreferences: async (preferences) => {
    await axios.put(`${API_URL}/notifications/preferences`, preferences);
  },

  // Get preferences
  getPreferences: async () => {
    const response = await axios.get(`${API_URL}/notifications/preferences`);
    return response.data;
  },

  // Get notification history
  getHistory: async () => {
    const response = await axios.get(`${API_URL}/notifications/history`);
    return response.data;
  },
};

export const analyticsService = {
  // Get analytics overview
  getOverview: async (eventId) => {
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/overview`);
    return response.data;
  },

  // Get sales funnel
  getFunnel: async (eventId) => {
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/funnel`);
    return response.data;
  },

  // Get traffic sources
  getTraffic: async (eventId) => {
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/traffic`);
    return response.data;
  },

  // Get demographics
  getDemographics: async (eventId) => {
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/demographics`);
    return response.data;
  },

  // Get revenue
  getRevenue: async (eventId) => {
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/revenue`);
    return response.data;
  },

  // Export CSV
  exportCSV: async (eventId) => {
    const response = await axios.get(`${API_URL}/analytics/events/${eventId}/export`);
    return response.data;
  },

  // Track view
  trackView: async (eventId, source = 'DIRECT_LINK') => {
    await axios.post(`${API_URL}/analytics/events/${eventId}/track/view?source=${source}`);
  },
};

export const ticketService = {
  // Get user tickets
  getMyTickets: async () => {
    const response = await axios.get(`${API_URL}/tickets/my-tickets`);
    return response.data;
  },

  // Get ticket details
  getTicket: async (ticketId) => {
    const response = await axios.get(`${API_URL}/tickets/${ticketId}`);
    return response.data;
  },

  // Mark as downloaded
  markAsDownloaded: async (ticketId) => {
    await axios.post(`${API_URL}/tickets/${ticketId}/download`);
  },

  // Get Apple Wallet pass
  getAppleWalletPass: async (ticketId) => {
    const response = await axios.get(`${API_URL}/tickets/${ticketId}/wallet/apple`);
    return response.data;
  },

  // Get Google Wallet pass
  getGoogleWalletPass: async (ticketId) => {
    const response = await axios.get(`${API_URL}/tickets/${ticketId}/wallet/google`);
    return response.data;
  },

  // Transfer ticket
  transferTicket: async (ticketId, recipientId) => {
    await axios.post(`${API_URL}/tickets/${ticketId}/transfer`, { recipientId });
  },
};

// PRIORITY 2 SERVICES

export const reviewService = {
  // Create review
  createReview: async (eventId, rating, comment, vibeTags) => {
    const response = await axios.post(`${API_URL}/reviews`, { eventId, rating, comment, vibeTags });
    return response.data;
  },

  // Get event reviews
  getEventReviews: async (eventId) => {
    const response = await axios.get(`${API_URL}/reviews/events/${eventId}`);
    return response.data;
  },

  // Get verified reviews
  getVerifiedReviews: async (eventId) => {
    const response = await axios.get(`${API_URL}/reviews/events/${eventId}/verified`);
    return response.data;
  },

  // Get average rating
  getAverageRating: async (eventId) => {
    const response = await axios.get(`${API_URL}/reviews/events/${eventId}/rating`);
    return response.data;
  },

  // Mark helpful
  markHelpful: async (reviewId) => {
    await axios.post(`${API_URL}/reviews/${reviewId}/helpful`);
  },
};

export const loyaltyService = {
  // Get user tier
  getUserTier: async () => {
    const response = await axios.get(`${API_URL}/loyalty/tier`);
    return response.data;
  },

  // Get points history
  getPointsHistory: async () => {
    const response = await axios.get(`${API_URL}/loyalty/points/history`);
    return response.data;
  },

  // Redeem points
  redeemPoints: async (points) => {
    await axios.post(`${API_URL}/loyalty/points/redeem`, { points });
  },
};

export const checkInService = {
  // Check in to event
  checkIn: async (ticketId) => {
    const response = await axios.post(`${API_URL}/checkin/tickets/${ticketId}`);
    return response.data;
  },

  // Get user check-ins
  getMyCheckIns: async () => {
    const response = await axios.get(`${API_URL}/checkin/my-checkins`);
    return response.data;
  },

  // Get check-in by ticket
  getCheckInByTicket: async (ticketId) => {
    const response = await axios.get(`${API_URL}/checkin/tickets/${ticketId}`);
    return response.data;
  },
};

export const memoryWallService = {
  // Upload memory
  uploadMemory: async (eventId, photoUrl, caption) => {
    const response = await axios.post(`${API_URL}/memories`, { eventId, photoUrl, caption });
    return response.data;
  },

  // Get event memories
  getEventMemories: async (eventId) => {
    const response = await axios.get(`${API_URL}/memories/events/${eventId}`);
    return response.data;
  },

  // Get my memories
  getMyMemories: async () => {
    const response = await axios.get(`${API_URL}/memories/my-memories`);
    return response.data;
  },

  // Like memory
  likeMemory: async (memoryId) => {
    await axios.post(`${API_URL}/memories/${memoryId}/like`);
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
    const response = await axios.get(`${API_URL}/discount-codes/events/${eventId}`);
    return response.data;
  },
};

export const groupTicketService = {
  // Create group ticket
  createGroupTicket: async (eventId, totalTickets, participantUserIds) => {
    const response = await axios.post(`${API_URL}/group-tickets`, { eventId, totalTickets, participantUserIds });
    return response.data;
  },

  // Get my group tickets
  getMyGroupTickets: async () => {
    const response = await axios.get(`${API_URL}/group-tickets/my-groups`);
    return response.data;
  },

  // Get group ticket
  getGroupTicket: async (groupTicketId) => {
    const response = await axios.get(`${API_URL}/group-tickets/${groupTicketId}`);
    return response.data;
  },

  // Pay for ticket
  payForTicket: async (participantId) => {
    await axios.post(`${API_URL}/group-tickets/participants/${participantId}/pay`);
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

// PRIORITY 3 SERVICES

export const moodSearchService = {
  // Search by mood
  searchByMood: async (moodQuery) => {
    const response = await axios.post(`${API_URL}/mood-search`, { query: moodQuery });
    return response.data;
  },

  // Get mood suggestions
  getMoodSuggestions: async () => {
    const response = await axios.get(`${API_URL}/mood-search/suggestions`);
    return response.data;
  },
};

export const weatherService = {
  // Get event weather
  getEventWeather: async (eventId) => {
    const response = await axios.get(`${API_URL}/weather/events/${eventId}`);
    return response.data;
  },

  // Get forecast
  getForecast: async (lat, lon, days = 5) => {
    const response = await axios.get(`${API_URL}/weather/forecast?lat=${lat}&lon=${lon}&days=${days}`);
    return response.data;
  },
};

export const badgeService = {
  // Get user badges
  getUserBadges: async () => {
    const response = await axios.get(`${API_URL}/badges/my-badges`);
    return response.data;
  },

  // Get all badges
  getAllBadges: async () => {
    const response = await axios.get(`${API_URL}/badges`);
    return response.data;
  },

  // Check badge progress
  checkProgress: async (badgeType) => {
    const response = await axios.get(`${API_URL}/badges/progress/${badgeType}`);
    return response.data;
  },
};

export const curatorService = {
  // Get curators
  getCurators: async () => {
    const response = await axios.get(`${API_URL}/curators`);
    return response.data;
  },

  // Get curator lists
  getCuratorLists: async (curatorId) => {
    const response = await axios.get(`${API_URL}/curators/${curatorId}/lists`);
    return response.data;
  },

  // Follow curator
  followCurator: async (curatorId) => {
    await axios.post(`${API_URL}/curators/${curatorId}/follow`);
  },

  // Unfollow curator
  unfollowCurator: async (curatorId) => {
    await axios.delete(`${API_URL}/curators/${curatorId}/unfollow`);
  },

  // Get followed curators
  getFollowedCurators: async () => {
    const response = await axios.get(`${API_URL}/curators/following`);
    return response.data;
  },
};

export const eventTemplateService = {
  // Get templates
  getTemplates: async () => {
    const response = await axios.get(`${API_URL}/event-templates`);
    return response.data;
  },

  // Create template
  createTemplate: async (eventId, name) => {
    const response = await axios.post(`${API_URL}/event-templates`, { eventId, name });
    return response.data;
  },

  // Use template
  useTemplate: async (templateId) => {
    const response = await axios.post(`${API_URL}/event-templates/${templateId}/use`);
    return response.data;
  },

  // Delete template
  deleteTemplate: async (templateId) => {
    await axios.delete(`${API_URL}/event-templates/${templateId}`);
  },
};

export const eventSeriesService = {
  // Get series
  getSeries: async (seriesId) => {
    const response = await axios.get(`${API_URL}/event-series/${seriesId}`);
    return response.data;
  },

  // Get series events
  getSeriesEvents: async (seriesId) => {
    const response = await axios.get(`${API_URL}/event-series/${seriesId}/events`);
    return response.data;
  },

  // Create series
  createSeries: async (name, description, eventIds) => {
    const response = await axios.post(`${API_URL}/event-series`, { name, description, eventIds });
    return response.data;
  },
};

export const cityGuideService = {
  // Get nearby locations
  getNearbyLocations: async (eventId, type) => {
    const response = await axios.get(`${API_URL}/city-guide/events/${eventId}/nearby?type=${type}`);
    return response.data;
  },

  // Get all locations
  getAllLocations: async (type) => {
    const response = await axios.get(`${API_URL}/city-guide/locations?type=${type}`);
    return response.data;
  },

  // Get travel time
  getTravelTime: async (eventId, fromLat, fromLon) => {
    const response = await axios.get(`${API_URL}/city-guide/events/${eventId}/travel-time?fromLat=${fromLat}&fromLon=${fromLon}`);
    return response.data;
  },
};

// PRIORITY 4 SERVICES

export const communityService = {
  // Get posts
  getPosts: async (boardType) => {
    const response = await axios.get(`${API_URL}/community/posts?boardType=${boardType}`);
    return response.data;
  },

  // Create post
  createPost: async (boardType, title, content) => {
    const response = await axios.post(`${API_URL}/community/posts`, { boardType, title, content });
    return response.data;
  },

  // Upvote post
  upvotePost: async (postId) => {
    await axios.post(`${API_URL}/community/posts/${postId}/upvote`);
  },

  // Comment on post
  commentOnPost: async (postId, content) => {
    const response = await axios.post(`${API_URL}/community/posts/${postId}/comments`, { content });
    return response.data;
  },

  // Get comments
  getComments: async (postId) => {
    const response = await axios.get(`${API_URL}/community/posts/${postId}/comments`);
    return response.data;
  },
};

export const translationService = {
  // Translate text
  translate: async (text, targetLanguage) => {
    const response = await axios.post(`${API_URL}/translations/translate`, { text, targetLanguage });
    return response.data;
  },

  // Get event translation
  getEventTranslation: async (eventId, language) => {
    const response = await axios.get(`${API_URL}/translations/events/${eventId}?language=${language}`);
    return response.data;
  },
};

export const liveStreamService = {
  // Start stream
  startStream: async (eventId) => {
    const response = await axios.post(`${API_URL}/live-streams/events/${eventId}/start`);
    return response.data;
  },

  // Stop stream
  stopStream: async (streamId) => {
    await axios.post(`${API_URL}/live-streams/${streamId}/stop`);
  },

  // Get active streams
  getActiveStreams: async () => {
    const response = await axios.get(`${API_URL}/live-streams/active`);
    return response.data;
  },

  // Get stream
  getStream: async (streamId) => {
    const response = await axios.get(`${API_URL}/live-streams/${streamId}`);
    return response.data;
  },

  // Send reaction
  sendReaction: async (streamId, reactionType) => {
    await axios.post(`${API_URL}/live-streams/${streamId}/reactions`, { reactionType });
  },
};

export const itineraryService = {
  // Get itineraries
  getItineraries: async () => {
    const response = await axios.get(`${API_URL}/itineraries`);
    return response.data;
  },

  // Create itinerary
  createItinerary: async (name, eventIds) => {
    const response = await axios.post(`${API_URL}/itineraries`, { name, eventIds });
    return response.data;
  },

  // Update itinerary
  updateItinerary: async (itineraryId, name, eventIds) => {
    const response = await axios.put(`${API_URL}/itineraries/${itineraryId}`, { name, eventIds });
    return response.data;
  },

  // Delete itinerary
  deleteItinerary: async (itineraryId) => {
    await axios.delete(`${API_URL}/itineraries/${itineraryId}`);
  },

  // Get itinerary
  getItinerary: async (itineraryId) => {
    const response = await axios.get(`${API_URL}/itineraries/${itineraryId}`);
    return response.data;
  },

  // Check conflicts
  checkConflicts: async (eventIds) => {
    const response = await axios.post(`${API_URL}/itineraries/check-conflicts`, { eventIds });
    return response.data;
  },
};

export default {
  personalizationService,
  socialService,
  filterService,
  notificationService,
  analyticsService,
  ticketService,
  reviewService,
  loyaltyService,
  checkInService,
  memoryWallService,
  happeningNowService,
  dynamicPricingService,
  discountCodeService,
  groupTicketService,
  bundleService,
  moodSearchService,
  weatherService,
  badgeService,
  curatorService,
  eventTemplateService,
  eventSeriesService,
  cityGuideService,
  communityService,
  translationService,
  liveStreamService,
  itineraryService,
};
