import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
  StatusBar,
  Dimensions,
  Alert,
  Share,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { BlurView } from 'expo-blur';
import { Ionicons } from '@expo/vector-icons';
import MapView, { Marker } from 'react-native-maps';
import Animated, {
  useAnimatedScrollHandler,
  useAnimatedStyle,
  interpolate,
  Extrapolate,
  FadeInDown,
  FadeInUp,
  useSharedValue,
  withSpring,
} from 'react-native-reanimated';
import * as Haptics from 'expo-haptics';
import api from '../services/api';
import { trackEventView, trackEventSave } from '../services/eventTracking';
import { cacheTickets } from '../services/offlineTickets';
import { scheduleTicketReminders } from '../services/localNotifications';

const { width, height } = Dimensions.get('window');
const HEADER_HEIGHT = 400;

const AnimatedScrollView = Animated.createAnimatedComponent(ScrollView);

export default function EventDetailScreen({ route, navigation }) {
  const { eventId } = route.params;
  const [event, setEvent] = useState(null);
  const [friendsAttending, setFriendsAttending] = useState(null);
  const [loading, setLoading] = useState(true);
  const scrollY = useSharedValue(0);
  const scale = useSharedValue(1);

  useEffect(() => {
    loadEvent();
  }, []);

  const loadEvent = async () => {
    try {
      const response = await api.get(`/events/${eventId}`);
      setEvent(response.data);
      trackEventView(eventId);
      try {
        const att = await api.get(`/social/events/${eventId}/attendees`);
        setFriendsAttending(att.data);
      } catch {
        /* optional */
      }
    } catch (error) {
      console.error('Error loading event:', error);
      Alert.alert('Error', 'Failed to load event details');
    } finally {
      setLoading(false);
    }
  };

  const scrollHandler = useAnimatedScrollHandler({
    onScroll: (event) => {
      scrollY.value = event.contentOffset.y;
    },
  });

  const headerAnimatedStyle = useAnimatedStyle(() => {
    const translateY = interpolate(
      scrollY.value,
      [0, HEADER_HEIGHT],
      [0, -HEADER_HEIGHT / 2],
      Extrapolate.CLAMP
    );

    const scale = interpolate(
      scrollY.value,
      [-100, 0, HEADER_HEIGHT],
      [1.3, 1, 0.9],
      Extrapolate.CLAMP
    );

    return {
      transform: [{ translateY }, { scale }],
    };
  });

  const headerOpacityStyle = useAnimatedStyle(() => {
    const opacity = interpolate(
      scrollY.value,
      [0, HEADER_HEIGHT / 2, HEADER_HEIGHT],
      [0, 0.5, 1],
      Extrapolate.CLAMP
    );

    return { opacity };
  });

  const titleOpacityStyle = useAnimatedStyle(() => {
    const opacity = interpolate(
      scrollY.value,
      [HEADER_HEIGHT - 100, HEADER_HEIGHT],
      [0, 1],
      Extrapolate.CLAMP
    );

    return { opacity };
  });

  const formatPrice = () => {
    if (!event) return 'Free';
    if (event.isFree || event.price == null || event.price === 0) {
      return 'Free';
    }
    return `${Math.round(event.price)} ALL`;
  };

  const isSoldOut =
    event?.maxAttendees != null &&
    (event.currentAttendees || 0) >= event.maxAttendees;

  const handleGetTicket = async () => {
    if (isSoldOut) {
      Alert.alert('Sold out', 'Tickets are no longer available for this event.');
      return;
    }
    try {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Heavy);
      const purchaseRes = await api.post(`/tickets/purchase/${eventId}`);
      const ticket = purchaseRes.data;
      const cached = await api.get('/tickets/my-tickets').catch(() => ({ data: [ticket] }));
      await cacheTickets(cached.data);
      await scheduleTicketReminders(ticket);
      Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
      Alert.alert('Success', 'Ticket purchased and saved for offline use!');
      navigation.navigate('Tickets');
    } catch (error) {
      Haptics.notificationAsync(Haptics.NotificationFeedbackType.Error);
      Alert.alert('Error', error.response?.data?.message || 'Failed to purchase ticket');
    }
  };

  const handleSave = async () => {
    try {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
      if (event.isSaved) {
        await api.delete(`/events/${eventId}/save`);
      } else {
        await api.post(`/events/${eventId}/save`);
      }
      const saved = !event.isSaved;
      setEvent({ ...event, isSaved: saved });
      if (saved) trackEventSave(eventId);
    } catch (error) {
      console.error('Error saving event:', error);
    }
  };

  const shareEvent = async () => {
    const link = `tiranaevents://event/${eventId}`;
    await Share.share({
      message: `${event.name}\n${event.location}\n${link}`,
      title: event.name,
    });
  };

  if (loading || !event) {
    return (
      <View style={[styles.container, styles.centered]}>
        <LinearGradient
          colors={['#0A0A0F', '#1A0B2E', '#0A0A0F']}
          style={StyleSheet.absoluteFill}
        />
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Background */}
      <LinearGradient
        colors={['#0A0A0F', '#1A0B2E', '#0A0A0F']}
        style={StyleSheet.absoluteFill}
      />

      {/* Animated Header Image */}
      <Animated.View style={[styles.headerImageContainer, headerAnimatedStyle]}>
        <Image
          source={{ uri: event.imageUrl || 'https://via.placeholder.com/400x300' }}
          style={styles.headerImage}
        />
        <LinearGradient
          colors={['transparent', 'rgba(0,0,0,0.3)', 'rgba(10,10,15,0.95)']}
          style={styles.headerGradient}
        />
      </Animated.View>

      {/* Floating Header Bar */}
      <Animated.View style={[styles.floatingHeader, headerOpacityStyle]}>
        <BlurView intensity={30} tint="dark" style={styles.floatingHeaderBlur}>
          <LinearGradient
            colors={['rgba(139,92,246,0.2)', 'rgba(109,40,217,0.1)']}
            style={styles.floatingHeaderContent}
          >
            <TouchableOpacity
              style={styles.backButton}
              onPress={() => {
                Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
                navigation.goBack();
              }}
            >
              <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
            </TouchableOpacity>
            
            <Animated.Text style={[styles.floatingTitle, titleOpacityStyle]} numberOfLines={1}>
              {event.name}
            </Animated.Text>

            <TouchableOpacity style={styles.shareButton} onPress={handleSave}>
              <Ionicons
                name={event.isSaved ? 'heart' : 'heart-outline'}
                size={24}
                color={event.isSaved ? '#EF4444' : '#FFFFFF'}
              />
            </TouchableOpacity>
          </LinearGradient>
        </BlurView>
      </Animated.View>

      {/* Action Buttons */}
      <View style={styles.actionButtons}>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => {
            Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
            navigation.goBack();
          }}
        >
          <BlurView intensity={20} tint="dark" style={styles.actionButtonBlur}>
            <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
          </BlurView>
        </TouchableOpacity>

        <View style={styles.actionButtonsRight}>
          <TouchableOpacity style={styles.actionButton} onPress={handleSave}>
            <BlurView intensity={20} tint="dark" style={styles.actionButtonBlur}>
              <Ionicons
                name={event.isSaved ? 'heart' : 'heart-outline'}
                size={24}
                color={event.isSaved ? '#EF4444' : '#FFFFFF'}
              />
            </BlurView>
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionButton} onPress={shareEvent}>
            <BlurView intensity={20} tint="dark" style={styles.actionButtonBlur}>
              <Ionicons name="share-outline" size={24} color="#FFFFFF" />
            </BlurView>
          </TouchableOpacity>
        </View>
      </View>

      <AnimatedScrollView
        onScroll={scrollHandler}
        scrollEventThrottle={16}
        showsVerticalScrollIndicator={false}
        contentContainerStyle={styles.scrollContent}
      >
        <View style={styles.contentContainer}>
          {/* Event Header */}
          <Animated.View
            entering={FadeInDown.delay(200).springify()}
            style={styles.eventHeader}
          >
            <View style={styles.categoryBadge}>
              <LinearGradient
                colors={['#8B5CF6', '#6D28D9']}
                style={styles.categoryBadgeGradient}
              >
                <Text style={styles.categoryBadgeText}>{event.categoryName}</Text>
              </LinearGradient>
            </View>

            <Text style={styles.eventTitle}>{event.name}</Text>

            <View style={styles.statsRow}>
              <View style={styles.statItem}>
                <Ionicons name="people" size={20} color="#8B5CF6" />
                <Text style={styles.statText}>{event.currentAttendees || 0} going</Text>
              </View>
              {friendsAttending?.count > 0 && (
                <>
                  <View style={styles.statDivider} />
                  <View style={styles.statItem}>
                    <Ionicons name="heart" size={20} color="#EC4899" />
                    <Text style={styles.statText}>
                      {friendsAttending.count} friend{friendsAttending.count > 1 ? 's' : ''}
                    </Text>
                  </View>
                </>
              )}
            </View>

            <ScrollView
              horizontal
              showsHorizontalScrollIndicator={false}
              style={styles.quickActions}
            >
              <TouchableOpacity
                style={styles.quickChip}
                onPress={() => navigation.navigate('EventChat', { eventId })}
              >
                <Ionicons name="chatbubbles-outline" size={16} color="#8B5CF6" />
                <Text style={styles.quickChipText}>Chat</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.quickChip}
                onPress={() => navigation.navigate('Reviews', { eventId })}
              >
                <Ionicons name="star-outline" size={16} color="#8B5CF6" />
                <Text style={styles.quickChipText}>Reviews</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.quickChip}
                onPress={() => navigation.navigate('GroupTickets', { eventId })}
              >
                <Ionicons name="people-outline" size={16} color="#8B5CF6" />
                <Text style={styles.quickChipText}>Group</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styles.quickChip}
                onPress={() => navigation.navigate('MemoryWall', { eventId })}
              >
                <Ionicons name="images-outline" size={16} color="#8B5CF6" />
                <Text style={styles.quickChipText}>Photos</Text>
              </TouchableOpacity>
            </ScrollView>
          </Animated.View>

          {/* Info Cards */}
          <Animated.View entering={FadeInDown.delay(300).springify()}>
            {/* Date & Time Card */}
            <View style={styles.infoCard}>
              <BlurView intensity={20} tint="dark" style={styles.infoCardBlur}>
                <LinearGradient
                  colors={['rgba(139,92,246,0.1)', 'rgba(109,40,217,0.05)']}
                  style={styles.infoCardContent}
                >
                  <View style={styles.infoIconContainer}>
                    <LinearGradient
                      colors={['#8B5CF6', '#6D28D9']}
                      style={styles.infoIconGradient}
                    >
                      <Ionicons name="calendar" size={24} color="#FFFFFF" />
                    </LinearGradient>
                  </View>
                  <View style={styles.infoTextContainer}>
                    <Text style={styles.infoLabel}>Date & Time</Text>
                    <Text style={styles.infoValue}>{formatDate(event.startDate)}</Text>
                    <Text style={styles.infoSubValue}>{formatTime(event.startDate)}</Text>
                  </View>
                  <TouchableOpacity style={styles.infoAction}>
                    <Ionicons name="add-circle-outline" size={24} color="#8B5CF6" />
                  </TouchableOpacity>
                </LinearGradient>
              </BlurView>
            </View>

            {/* Location Card */}
            <View style={styles.infoCard}>
              <BlurView intensity={20} tint="dark" style={styles.infoCardBlur}>
                <LinearGradient
                  colors={['rgba(139,92,246,0.1)', 'rgba(109,40,217,0.05)']}
                  style={styles.infoCardContent}
                >
                  <View style={styles.infoIconContainer}>
                    <LinearGradient
                      colors={['#EC4899', '#BE185D']}
                      style={styles.infoIconGradient}
                    >
                      <Ionicons name="location" size={24} color="#FFFFFF" />
                    </LinearGradient>
                  </View>
                  <View style={styles.infoTextContainer}>
                    <Text style={styles.infoLabel}>Location</Text>
                    <Text style={styles.infoValue}>{event.location}</Text>
                  </View>
                  <TouchableOpacity style={styles.infoAction}>
                    <Ionicons name="navigate-outline" size={24} color="#EC4899" />
                  </TouchableOpacity>
                </LinearGradient>
              </BlurView>
            </View>

            {/* Organizer Card */}
            <View style={styles.infoCard}>
              <BlurView intensity={20} tint="dark" style={styles.infoCardBlur}>
                <LinearGradient
                  colors={['rgba(139,92,246,0.1)', 'rgba(109,40,217,0.05)']}
                  style={styles.infoCardContent}
                >
                  <View style={styles.organizerAvatar}>
                    <LinearGradient
                      colors={['#3B82F6', '#1D4ED8']}
                      style={styles.organizerAvatarGradient}
                    >
                      <Text style={styles.organizerInitial}>
                        {event.organizerName?.charAt(0) || 'O'}
                      </Text>
                    </LinearGradient>
                  </View>
                  <View style={styles.infoTextContainer}>
                    <Text style={styles.infoLabel}>Organizer</Text>
                    <Text style={styles.infoValue}>{event.organizerName}</Text>
                  </View>
                  <TouchableOpacity style={styles.followButton}>
                    <LinearGradient
                      colors={['#8B5CF6', '#6D28D9']}
                      style={styles.followGradient}
                    >
                      <Text style={styles.followText}>Follow</Text>
                    </LinearGradient>
                  </TouchableOpacity>
                </LinearGradient>
              </BlurView>
            </View>
          </Animated.View>

          {/* About Section */}
          <Animated.View
            entering={FadeInDown.delay(400).springify()}
            style={styles.section}
          >
            <Text style={styles.sectionTitle}>About Event</Text>
            <View style={styles.aboutCard}>
              <BlurView intensity={20} tint="dark" style={styles.aboutBlur}>
                <LinearGradient
                  colors={['rgba(139,92,246,0.1)', 'rgba(109,40,217,0.05)']}
                  style={styles.aboutContent}
                >
                  <Text style={styles.description}>
                    {event.description || 'No description available.'}
                  </Text>
                </LinearGradient>
              </BlurView>
            </View>
          </Animated.View>

          {/* Map Section */}
          {event.latitude && event.longitude && (
            <Animated.View
              entering={FadeInDown.delay(500).springify()}
              style={styles.section}
            >
              <Text style={styles.sectionTitle}>Location</Text>
              <View style={styles.mapCard}>
                <MapView
                  style={styles.map}
                  initialRegion={{
                    latitude: event.latitude,
                    longitude: event.longitude,
                    latitudeDelta: 0.01,
                    longitudeDelta: 0.01,
                  }}
                  scrollEnabled={false}
                  zoomEnabled={false}
                  customMapStyle={mapStyle}
                >
                  <Marker
                    coordinate={{
                      latitude: event.latitude,
                      longitude: event.longitude,
                    }}
                  >
                    <View style={styles.markerContainer}>
                      <LinearGradient
                        colors={['#8B5CF6', '#6D28D9']}
                        style={styles.marker}
                      >
                        <Ionicons name="location" size={20} color="#FFFFFF" />
                      </LinearGradient>
                    </View>
                  </Marker>
                </MapView>
              </View>
            </Animated.View>
          )}

          <View style={{ height: 120 }} />
        </View>
      </AnimatedScrollView>

      {/* Floating Ticket Button */}
      <Animated.View entering={FadeInUp.delay(600).springify()} style={styles.footer}>
        <BlurView intensity={30} tint="dark" style={styles.footerBlur}>
          <LinearGradient
            colors={['rgba(10,10,15,0.95)', 'rgba(10,10,15,0.8)']}
            style={styles.footerContent}
          >
            <View style={styles.priceContainer}>
              <Text style={styles.priceLabel}>Price</Text>
              <Text style={styles.priceValue}>{formatPrice()}</Text>
            </View>
            <TouchableOpacity
              style={[styles.ticketButton, isSoldOut && styles.ticketButtonDisabled]}
              onPress={handleGetTicket}
              activeOpacity={0.8}
              disabled={isSoldOut}
            >
              <LinearGradient
                colors={isSoldOut ? ['#4B5563', '#374151'] : ['#8B5CF6', '#6D28D9']}
                start={{ x: 0, y: 0 }}
                end={{ x: 1, y: 1 }}
                style={styles.ticketButtonGradient}
              >
                <Text style={styles.ticketButtonText}>
                  {isSoldOut ? 'Sold Out' : 'Get Ticket'}
                </Text>
                {!isSoldOut && (
                  <Ionicons name="arrow-forward" size={20} color="#FFFFFF" />
                )}
              </LinearGradient>
            </TouchableOpacity>
          </LinearGradient>
        </BlurView>
      </Animated.View>
    </View>
  );
}

const mapStyle = [
  { elementType: 'geometry', stylers: [{ color: '#1A1A24' }] },
  { elementType: 'labels.text.fill', stylers: [{ color: '#9CA3AF' }] },
  { elementType: 'labels.text.stroke', stylers: [{ color: '#0A0A0F' }] },
  { featureType: 'road', elementType: 'geometry', stylers: [{ color: '#2A2A3C' }] },
  { featureType: 'water', elementType: 'geometry', stylers: [{ color: '#0A0A0F' }] },
];

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  centered: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    color: '#FFFFFF',
    fontSize: 16,
  },
  headerImageContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: HEADER_HEIGHT,
    zIndex: 0,
  },
  headerImage: {
    width: '100%',
    height: '100%',
  },
  headerGradient: {
    ...StyleSheet.absoluteFillObject,
  },
  floatingHeader: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    zIndex: 100,
    paddingTop: 50,
  },
  floatingHeaderBlur: {
    borderBottomWidth: 1,
    borderBottomColor: 'rgba(139,92,246,0.2)',
  },
  floatingHeaderContent: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 12,
  },
  backButton: {
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  floatingTitle: {
    flex: 1,
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginHorizontal: 16,
  },
  shareButton: {
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  actionButtons: {
    position: 'absolute',
    top: 60,
    left: 0,
    right: 0,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    zIndex: 10,
  },
  actionButtonsRight: {
    flexDirection: 'row',
    gap: 12,
  },
  actionButton: {
    width: 48,
    height: 48,
    borderRadius: 24,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.1)',
  },
  actionButtonBlur: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scrollContent: {
    paddingTop: HEADER_HEIGHT - 50,
  },
  contentContainer: {
    paddingHorizontal: 20,
  },
  eventHeader: {
    marginBottom: 24,
  },
  categoryBadge: {
    alignSelf: 'flex-start',
    borderRadius: 16,
    overflow: 'hidden',
    marginBottom: 16,
  },
  categoryBadgeGradient: {
    paddingHorizontal: 16,
    paddingVertical: 8,
  },
  categoryBadgeText: {
    color: '#FFFFFF',
    fontSize: 12,
    fontWeight: 'bold',
    letterSpacing: 1,
  },
  eventTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
    letterSpacing: -0.5,
  },
  statsRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statItem: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statText: {
    color: '#9CA3AF',
    fontSize: 14,
    marginLeft: 8,
    fontWeight: '600',
  },
  statDivider: {
    width: 1,
    height: 16,
    backgroundColor: 'rgba(139,92,246,0.3)',
    marginHorizontal: 16,
  },
  quickActions: {
    marginTop: 16,
  },
  quickChip: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(139,92,246,0.15)',
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 20,
    marginRight: 10,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.3)',
  },
  quickChipText: {
    color: '#C4B5FD',
    fontSize: 13,
    fontWeight: '600',
    marginLeft: 6,
  },
  infoCard: {
    borderRadius: 20,
    overflow: 'hidden',
    marginBottom: 16,
  },
  infoCardBlur: {
    borderRadius: 20,
  },
  infoCardContent: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.2)',
  },
  infoIconContainer: {
    width: 56,
    height: 56,
    borderRadius: 28,
    overflow: 'hidden',
    marginRight: 16,
  },
  infoIconGradient: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  infoTextContainer: {
    flex: 1,
  },
  infoLabel: {
    fontSize: 12,
    color: '#6B7280',
    marginBottom: 4,
  },
  infoValue: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  infoSubValue: {
    fontSize: 14,
    color: '#9CA3AF',
    marginTop: 2,
  },
  infoAction: {
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  organizerAvatar: {
    width: 56,
    height: 56,
    borderRadius: 28,
    overflow: 'hidden',
    marginRight: 16,
  },
  organizerAvatarGradient: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  organizerInitial: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  followButton: {
    borderRadius: 12,
    overflow: 'hidden',
  },
  followGradient: {
    paddingHorizontal: 20,
    paddingVertical: 10,
  },
  followText: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
    letterSpacing: -0.5,
  },
  aboutCard: {
    borderRadius: 20,
    overflow: 'hidden',
  },
  aboutBlur: {
    borderRadius: 20,
  },
  aboutContent: {
    padding: 20,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.2)',
  },
  description: {
    fontSize: 15,
    color: '#9CA3AF',
    lineHeight: 24,
  },
  mapCard: {
    height: 200,
    borderRadius: 20,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.2)',
  },
  map: {
    flex: 1,
  },
  markerContainer: {
    alignItems: 'center',
  },
  marker: {
    width: 48,
    height: 48,
    borderRadius: 24,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 3,
    borderColor: '#FFFFFF',
  },
  footer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
  },
  footerBlur: {
    borderTopWidth: 1,
    borderTopColor: 'rgba(139,92,246,0.2)',
  },
  footerContent: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 16,
    paddingBottom: 32,
  },
  priceContainer: {
    marginRight: 16,
  },
  priceLabel: {
    fontSize: 12,
    color: '#6B7280',
    marginBottom: 4,
  },
  priceValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#10B981',
  },
  ticketButtonDisabled: {
    opacity: 0.85,
  },
  ticketButton: {
    flex: 1,
    borderRadius: 16,
    overflow: 'hidden',
    elevation: 8,
    shadowColor: '#8B5CF6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.4,
    shadowRadius: 12,
  },
  ticketButtonGradient: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 18,
  },
  ticketButtonText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
    marginRight: 8,
    letterSpacing: 0.5,
  },
});
