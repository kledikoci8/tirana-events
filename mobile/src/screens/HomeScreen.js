import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  Image,
  FlatList,
  StatusBar,
  Dimensions,
  ScrollView,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { BlurView } from 'expo-blur';
import { Ionicons } from '@expo/vector-icons';
import Animated, {
  FadeInDown,
  FadeInRight,
  useAnimatedStyle,
  useSharedValue,
  withSpring,
  withRepeat,
  withTiming,
  interpolate,
  Extrapolate,
} from 'react-native-reanimated';
import * as Haptics from 'expo-haptics';
import api from '../services/api';
import { trackEventView } from '../services/eventTracking';

const { width, height } = Dimensions.get('window');
const CARD_WIDTH = width - 40;

export default function HomeScreen({ navigation }) {
  const [events, setEvents] = useState([]);
  const [categories, setCategories] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [friendsByEvent, setFriendsByEvent] = useState({});
  const scrollY = useSharedValue(0);
  const pulseAnim = useSharedValue(1);

  useEffect(() => {
    loadData();
    // Pulse animation for notification badge
    pulseAnim.value = withRepeat(
      withTiming(1.2, { duration: 1000 }),
      -1,
      true
    );
  }, []);

  const loadData = async () => {
    try {
      const categoriesRes = await api.get('/categories');
      setCategories(categoriesRes.data);

      let list = [];
      try {
        const eventsRes = await api.get('/events/recommended?limit=20');
        list = eventsRes.data;
      } catch {
        const eventsRes = await api.get('/events/upcoming');
        list = eventsRes.data;
      }
      setEvents(list);
      loadFriendsAttending(list);
    } catch (error) {
      console.error('Error loading data:', error);
    }
  };

  const loadFriendsAttending = async (eventList) => {
    const map = {};
    await Promise.all(
      (eventList || []).slice(0, 12).map(async (ev) => {
        try {
          const res = await api.get(`/social/events/${ev.id}/attendees`);
          if (res.data?.count > 0) map[ev.id] = res.data;
        } catch {
          /* ignore */
        }
      })
    );
    setFriendsByEvent(map);
  };

  const applyCategoryFilter = async (categoryId) => {
    if (!categoryId) {
      loadData();
      return;
    }
    try {
      const res = await api.post('/filters/events', {
        categoryIds: [categoryId],
        page: 0,
        size: 30,
      });
      setEvents(res.data);
      loadFriendsAttending(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      loadData();
      return;
    }
    try {
      const response = await api.get(`/events/search?query=${searchQuery}`);
      setEvents(response.data);
    } catch (error) {
      console.error('Error searching:', error);
    }
  };

  const formatPrice = (item) => {
    if (item.isFree || item.price == null || item.price === 0) {
      return 'Free';
    }
    return `${Math.round(item.price)} ALL`;
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'short' }).toUpperCase();
    return { day, month };
  };

  const headerAnimatedStyle = useAnimatedStyle(() => {
    const opacity = interpolate(
      scrollY.value,
      [0, 100],
      [1, 0],
      Extrapolate.CLAMP
    );
    return { opacity };
  });

  const badgeAnimatedStyle = useAnimatedStyle(() => ({
    transform: [{ scale: pulseAnim.value }],
  }));

  const renderCategory = ({ item, index }) => (
    <Animated.View
      entering={FadeInRight.delay(index * 100).springify()}
    >
      <TouchableOpacity
        style={styles.categoryItem}
        onPress={() => {
          Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
          const next = item.id === selectedCategory ? null : item.id;
          setSelectedCategory(next);
          applyCategoryFilter(next);
        }}
      >
        <LinearGradient
          colors={
            selectedCategory === item.id
              ? [item.color, item.color + 'CC']
              : ['rgba(255,255,255,0.1)', 'rgba(255,255,255,0.05)']
          }
          style={styles.categoryIconContainer}
        >
          <BlurView intensity={20} tint="dark" style={styles.categoryBlur}>
            <Ionicons 
              name={item.icon} 
              size={32} 
              color={selectedCategory === item.id ? '#FFFFFF' : '#8B5CF6'} 
            />
          </BlurView>
        </LinearGradient>
        <Text style={styles.categoryText}>{item.name}</Text>
        {selectedCategory === item.id && (
          <View style={[styles.categoryIndicator, { backgroundColor: item.color }]} />
        )}
      </TouchableOpacity>
    </Animated.View>
  );

  const renderEvent = ({ item, index }) => {
    const { day, month } = formatDate(item.startDate);
    
    return (
      <Animated.View
        entering={FadeInDown.delay(index * 150).springify()}
        style={styles.eventCardWrapper}
      >
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
            trackEventView(item.id);
            navigation.navigate('EventDetail', { eventId: item.id });
          }}
        >
          <View style={styles.eventCard}>
            {/* Background Image with Overlay */}
            <Image
              source={{ uri: item.imageUrl || 'https://via.placeholder.com/400x200' }}
              style={styles.eventImage}
              blurRadius={0.5}
            />
            
            {/* Gradient Overlays */}
            <LinearGradient
              colors={['transparent', 'rgba(0,0,0,0.4)', 'rgba(0,0,0,0.95)']}
              style={styles.eventGradient}
            />
            
            {/* Glass Card Overlay */}
            <BlurView intensity={15} tint="dark" style={styles.eventContentBlur}>
              <LinearGradient
                colors={['rgba(139,92,246,0.15)', 'rgba(109,40,217,0.1)']}
                style={styles.eventContent}
              >
                {/* Date Badge */}
                <View style={styles.eventDateBadge}>
                  <LinearGradient
                    colors={['#8B5CF6', '#6D28D9']}
                    style={styles.dateBadgeGradient}
                  >
                    <Text style={styles.eventDay}>{day}</Text>
                    <Text style={styles.eventMonth}>{month}</Text>
                  </LinearGradient>
                </View>

                {/* Event Info */}
                <View style={styles.eventInfo}>
                  <View style={styles.eventHeader}>
                    <View style={styles.categoryBadge}>
                      <Text style={styles.categoryBadgeText}>{item.categoryName}</Text>
                    </View>
                    <TouchableOpacity
                      style={styles.favoriteButton}
                      onPress={() => Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light)}
                    >
                      <Ionicons
                        name={item.isSaved ? 'heart' : 'heart-outline'}
                        size={20}
                        color={item.isSaved ? '#EF4444' : '#FFFFFF'}
                      />
                    </TouchableOpacity>
                  </View>

                  <Text style={styles.eventTitle} numberOfLines={2}>
                    {item.name}
                  </Text>

                  <View style={styles.eventMeta}>
                    <View style={styles.metaItem}>
                      <Ionicons name="location" size={14} color="#8B5CF6" />
                      <Text style={styles.metaText} numberOfLines={1}>
                        {item.location}
                      </Text>
                    </View>
                  </View>

                  {friendsByEvent[item.id]?.count > 0 && (
                    <View style={styles.friendsBanner}>
                      <Ionicons name="people" size={14} color="#A78BFA" />
                      <Text style={styles.friendsBannerText}>
                        {friendsByEvent[item.id].count} friend
                        {friendsByEvent[item.id].count > 1 ? 's' : ''} going
                      </Text>
                    </View>
                  )}

                  <View style={styles.eventFooter}>
                    <View style={styles.attendeesContainer}>
                      <Text style={styles.attendeesText}>
                        {item.currentAttendees || 0}+ going
                      </Text>
                    </View>

                    <View style={styles.priceTag}>
                      <Ionicons name="ticket" size={14} color="#10B981" />
                      <Text style={styles.priceText}>{formatPrice(item)}</Text>
                    </View>
                  </View>
                </View>
              </LinearGradient>
            </BlurView>

            {/* Trending Badge */}
            {index < 2 && (
              <View style={styles.trendingBadge}>
                <LinearGradient
                  colors={['#EF4444', '#DC2626']}
                  style={styles.trendingGradient}
                >
                  <Ionicons name="flame" size={12} color="#FFF" />
                  <Text style={styles.trendingText}>Trending</Text>
                </LinearGradient>
              </View>
            )}
          </View>
        </TouchableOpacity>
      </Animated.View>
    );
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Animated Background */}
      <LinearGradient
        colors={['#0A0A0F', '#1A0B2E', '#0A0A0F']}
        style={StyleSheet.absoluteFill}
      />

      <FlatList
        data={events}
        renderItem={renderEvent}
        keyExtractor={(item) => item.id.toString()}
        showsVerticalScrollIndicator={false}
        contentContainerStyle={styles.eventsList}
        onScroll={(e) => {
          scrollY.value = e.nativeEvent.contentOffset.y;
        }}
        scrollEventThrottle={16}
        ListHeaderComponent={
          <>
            {/* Header */}
            <Animated.View style={[styles.header, headerAnimatedStyle]}>
              <View style={styles.headerTop}>
                <View style={styles.logoWrapper}>
                  <View style={styles.logoIconWrapper}>
                    <Ionicons name="calendar" size={20} color="#8B5CF6" />
                  </View>
                  <View>
                    <Text style={styles.logo}>TIRANA</Text>
                    <Text style={styles.logoSub}>EVENTS</Text>
                  </View>
                </View>
                <TouchableOpacity
                  style={styles.notificationButton}
                  onPress={() => navigation.navigate('NotificationsInbox')}
                >
                  <BlurView intensity={20} tint="dark" style={styles.notificationBlur}>
                    <Ionicons name="notifications-outline" size={24} color="#FFFFFF" />
                    <Animated.View style={[styles.notificationBadge, badgeAnimatedStyle]} />
                  </BlurView>
                </TouchableOpacity>
              </View>

              {/* Title Section */}
              <Animated.View
                entering={FadeInDown.delay(200).springify()}
                style={styles.titleSection}
              >
                <Text style={styles.title}>What's happening</Text>
                <View style={styles.titleHighlightContainer}>
                  <Text style={styles.titleHighlight}>in Tirana </Text>
                  <Text style={styles.titleHighlight}>today?</Text>
                  <Ionicons name="flame" size={32} color="#EF4444" style={{ marginLeft: 8 }} />
                </View>
              </Animated.View>

              {/* Search Bar */}
              <Animated.View
                entering={FadeInDown.delay(300).springify()}
                style={styles.searchWrapper}
              >
                <BlurView intensity={30} tint="dark" style={styles.searchBlur}>
                  <LinearGradient
                    colors={['rgba(139,92,246,0.1)', 'rgba(109,40,217,0.05)']}
                    style={styles.searchContainer}
                  >
                    <Ionicons name="search-outline" size={20} color="#8B5CF6" />
                    <TextInput
                      style={styles.searchInput}
                      placeholder="Search events, concerts, festivals..."
                      placeholderTextColor="#6B7280"
                      value={searchQuery}
                      onChangeText={setSearchQuery}
                      onSubmitEditing={handleSearch}
                    />
                    <TouchableOpacity
                      style={styles.filterButton}
                      onPress={() =>
                        navigation.navigate('Filter', {
                          onApplyFilters: (data) => {
                            setEvents(data);
                            loadFriendsAttending(data);
                          },
                        })
                      }
                    >
                      <LinearGradient
                        colors={['#8B5CF6', '#6D28D9']}
                        style={styles.filterGradient}
                      >
                        <Ionicons name="options-outline" size={18} color="#FFFFFF" />
                      </LinearGradient>
                    </TouchableOpacity>
                  </LinearGradient>
                </BlurView>
              </Animated.View>
            </Animated.View>

            {/* Categories */}
            <View style={styles.section}>
              <View style={styles.sectionHeader}>
                <Text style={styles.sectionTitle}>Categories</Text>
                <TouchableOpacity>
                  <Text style={styles.seeAll}>See all →</Text>
                </TouchableOpacity>
              </View>
              <FlatList
                horizontal
                data={categories}
                renderItem={renderCategory}
                keyExtractor={(item) => item.id.toString()}
                showsHorizontalScrollIndicator={false}
                contentContainerStyle={styles.categoriesList}
              />
            </View>

            {/* Recommended Events Header */}
            <View style={styles.section}>
              <View style={styles.sectionHeader}>
                <View>
                  <Text style={styles.sectionTitle}>Recommended for you</Text>
                  <Text style={styles.sectionSubtitle}>Based on your interests</Text>
                </View>
                <TouchableOpacity>
                  <Text style={styles.seeAll}>See all →</Text>
                </TouchableOpacity>
              </View>
            </View>
          </>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  header: {
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 24,
  },
  headerTop: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 24,
  },
  logoWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  logoIconWrapper: {
    width: 40,
    height: 40,
    borderRadius: 12,
    backgroundColor: 'rgba(139,92,246,0.2)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.3)',
  },
  logo: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFFFFF',
    letterSpacing: 1,
  },
  logoSub: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#8B5CF6',
    marginTop: -4,
    letterSpacing: 2,
  },
  notificationButton: {
    width: 48,
    height: 48,
    borderRadius: 24,
    overflow: 'hidden',
  },
  notificationBlur: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  notificationBadge: {
    position: 'absolute',
    top: 12,
    right: 12,
    width: 10,
    height: 10,
    borderRadius: 5,
    backgroundColor: '#EF4444',
    borderWidth: 2,
    borderColor: '#0A0A0F',
  },
  titleSection: {
    marginBottom: 24,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    letterSpacing: -0.5,
  },
  titleHighlightContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  titleHighlight: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#8B5CF6',
    letterSpacing: -0.5,
  },
  searchWrapper: {
    borderRadius: 20,
    overflow: 'hidden',
  },
  searchBlur: {
    borderRadius: 20,
  },
  searchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 4,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.3)',
  },
  searchInput: {
    flex: 1,
    color: '#FFFFFF',
    fontSize: 14,
    paddingVertical: 12,
    marginLeft: 12,
  },
  filterButton: {
    width: 36,
    height: 36,
    borderRadius: 18,
    overflow: 'hidden',
  },
  filterGradient: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  section: {
    marginBottom: 32,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFFFFF',
    letterSpacing: -0.5,
  },
  sectionSubtitle: {
    fontSize: 12,
    color: '#6B7280',
    marginTop: 2,
  },
  seeAll: {
    fontSize: 14,
    color: '#8B5CF6',
    fontWeight: '600',
  },
  categoriesList: {
    paddingHorizontal: 20,
  },
  categoryItem: {
    alignItems: 'center',
    marginRight: 20,
  },
  categoryIconContainer: {
    width: 72,
    height: 72,
    borderRadius: 20,
    overflow: 'hidden',
    marginBottom: 8,
  },
  categoryBlur: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  categoryText: {
    fontSize: 12,
    color: '#FFFFFF',
    fontWeight: '600',
  },
  categoryIndicator: {
    width: 4,
    height: 4,
    borderRadius: 2,
    marginTop: 4,
  },
  eventsList: {
    paddingHorizontal: 20,
    paddingBottom: 100,
  },
  eventCardWrapper: {
    marginBottom: 20,
  },
  eventCard: {
    height: 320,
    borderRadius: 24,
    overflow: 'hidden',
  },
  eventImage: {
    ...StyleSheet.absoluteFillObject,
    width: '100%',
    height: '100%',
  },
  eventGradient: {
    ...StyleSheet.absoluteFillObject,
  },
  eventContentBlur: {
    ...StyleSheet.absoluteFillObject,
  },
  eventContent: {
    flex: 1,
    padding: 20,
    justifyContent: 'space-between',
  },
  eventDateBadge: {
    position: 'absolute',
    top: 20,
    left: 20,
    borderRadius: 16,
    overflow: 'hidden',
    elevation: 8,
    shadowColor: '#8B5CF6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
  },
  dateBadgeGradient: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    alignItems: 'center',
  },
  eventDay: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  eventMonth: {
    fontSize: 10,
    color: '#FFFFFF',
    marginTop: -4,
    letterSpacing: 1,
  },
  eventInfo: {
    marginTop: 'auto',
  },
  eventHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  categoryBadge: {
    backgroundColor: 'rgba(139,92,246,0.3)',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.5)',
  },
  categoryBadgeText: {
    color: '#FFFFFF',
    fontSize: 11,
    fontWeight: '600',
    letterSpacing: 0.5,
  },
  favoriteButton: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  eventTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
    letterSpacing: -0.5,
  },
  eventMeta: {
    marginBottom: 12,
  },
  metaItem: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  metaText: {
    fontSize: 13,
    color: '#9CA3AF',
    marginLeft: 6,
    flex: 1,
  },
  eventFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  attendeesContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  avatarStack: {
    flexDirection: 'row',
    marginRight: 8,
  },
  avatar: {
    width: 24,
    height: 24,
    borderRadius: 12,
    overflow: 'hidden',
    borderWidth: 2,
    borderColor: '#0A0A0F',
  },
  avatarGradient: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  friendsBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(139,92,246,0.2)',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 8,
    marginBottom: 8,
    alignSelf: 'flex-start',
  },
  friendsBannerText: {
    color: '#C4B5FD',
    fontSize: 12,
    fontWeight: '600',
    marginLeft: 6,
  },
  attendeesText: {
    fontSize: 12,
    color: '#9CA3AF',
    fontWeight: '600',
  },
  priceTag: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(16,185,129,0.2)',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: 'rgba(16,185,129,0.3)',
  },
  priceText: {
    fontSize: 12,
    color: '#10B981',
    fontWeight: 'bold',
    marginLeft: 4,
  },
  trendingBadge: {
    position: 'absolute',
    top: 20,
    right: 20,
    borderRadius: 12,
    overflow: 'hidden',
  },
  trendingGradient: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  trendingText: {
    fontSize: 10,
    color: '#FFFFFF',
    fontWeight: 'bold',
    marginLeft: 4,
    letterSpacing: 0.5,
  },
});
