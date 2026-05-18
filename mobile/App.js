import './src/polyfills/textEncoding';
import React, { useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as Font from 'expo-font';
import { View, ActivityIndicator, LogBox } from 'react-native';
import api from './src/services/api';
import { linking } from './src/navigation/linking';

LogBox.ignoreLogs(['VirtualizedLists should never be nested']);

import LoginScreen from './src/screens/LoginScreen';
import RegisterScreen from './src/screens/RegisterScreen';
import OnboardingScreen from './src/screens/OnboardingScreen';
import HomeScreen from './src/screens/HomeScreen';
import ExploreScreen from './src/screens/ExploreScreen';
import CreateEventScreen from './src/screens/CreateEventScreen';
import TicketsScreen from './src/screens/TicketsScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import EventDetailScreen from './src/screens/EventDetailScreen';
import FilterScreen from './src/screens/FilterScreen';
import FriendsScreen from './src/screens/FriendsScreen';
import EventChatScreen from './src/screens/EventChatScreen';
import NotificationSettingsScreen from './src/screens/NotificationSettingsScreen';
import NotificationsInboxScreen from './src/screens/NotificationsInboxScreen';
import OrganizerDashboardScreen from './src/screens/OrganizerDashboardScreen';
import MyEventsScreen from './src/screens/MyEventsScreen';
import HappeningNowScreen from './src/screens/HappeningNowScreen';
import ReviewScreen from './src/screens/ReviewScreen';
import MemoryWallScreen from './src/screens/MemoryWallScreen';
import GroupTicketScreen from './src/screens/GroupTicketScreen';
import LoyaltyScreen from './src/screens/LoyaltyScreen';
import BadgesScreen from './src/screens/BadgesScreen';
import MoodSearchScreen from './src/screens/MoodSearchScreen';
import CuratorListsScreen from './src/screens/CuratorListsScreen';
import CommunityBoardScreen from './src/screens/CommunityBoardScreen';
import ItineraryPlannerScreen from './src/screens/ItineraryPlannerScreen';

import { AuthProvider, useAuth } from './src/context/AuthContext';
import { ensureNotificationPermissions } from './src/services/localNotifications';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const modalOptions = {
  presentation: 'modal',
  animation: 'slide_from_bottom',
};

function TabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarStyle: {
          backgroundColor: '#1A1A24',
          borderTopColor: '#2A2A3C',
          height: 90,
          paddingBottom: 30,
          paddingTop: 10,
        },
        tabBarActiveTintColor: '#8B5CF6',
        tabBarInactiveTintColor: '#6B7280',
        tabBarIcon: ({ focused, color, size }) => {
          const icons = {
            Home: focused ? 'home' : 'home-outline',
            Explore: focused ? 'compass' : 'compass-outline',
            Create: focused ? 'add-circle' : 'add-circle-outline',
            Tickets: focused ? 'ticket' : 'ticket-outline',
            Profile: focused ? 'person' : 'person-outline',
          };
          return <Ionicons name={icons[route.name]} size={size} color={color} />;
        },
      })}
    >
      <Tab.Screen name="Home" component={HomeScreen} />
      <Tab.Screen name="Explore" component={ExploreScreen} />
      <Tab.Screen name="Create" component={CreateEventScreen} />
      <Tab.Screen name="Tickets" component={TicketsScreen} />
      <Tab.Screen name="Profile" component={ProfileScreen} />
    </Tab.Navigator>
  );
}

function AppNavigator() {
  const { user, loading } = useAuth();
  const [onboardingChecked, setOnboardingChecked] = useState(false);
  const [needsOnboarding, setNeedsOnboarding] = useState(false);

  useEffect(() => {
    if (!user) {
      setOnboardingChecked(true);
      setNeedsOnboarding(false);
      return;
    }
    checkOnboarding();
    ensureNotificationPermissions();
  }, [user]);

  const checkOnboarding = async () => {
    try {
      const local = await AsyncStorage.getItem('onboardingCompleted');
      if (local === 'true') {
        setNeedsOnboarding(false);
        setOnboardingChecked(true);
        return;
      }
      const res = await api.get('/personalization/onboarding/status');
      const needs = res.data === true;
      setNeedsOnboarding(needs);
      if (!needs) {
        await AsyncStorage.setItem('onboardingCompleted', 'true');
      }
    } catch {
      setNeedsOnboarding(false);
    } finally {
      setOnboardingChecked(true);
    }
  };

  if (loading || (user && !onboardingChecked)) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#0A0A0F' }}>
        <ActivityIndicator size="large" color="#8B5CF6" />
      </View>
    );
  }

  return (
    <Stack.Navigator screenOptions={{ headerShown: false, contentStyle: { backgroundColor: '#0A0A0F' } }}>
      {!user ? (
        <>
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
        </>
      ) : needsOnboarding ? (
        <Stack.Screen name="Onboarding" component={OnboardingScreen} />
      ) : (
        <>
          <Stack.Screen name="Main" component={TabNavigator} />
          <Stack.Screen name="EventDetail" component={EventDetailScreen} />
          <Stack.Screen name="Filter" component={FilterScreen} options={modalOptions} />
          <Stack.Screen name="Friends" component={FriendsScreen} />
          <Stack.Screen name="EventChat" component={EventChatScreen} />
          <Stack.Screen name="NotificationSettings" component={NotificationSettingsScreen} />
          <Stack.Screen name="NotificationsInbox" component={NotificationsInboxScreen} />
          <Stack.Screen name="MyEvents" component={MyEventsScreen} />
          <Stack.Screen name="OrganizerDashboard" component={OrganizerDashboardScreen} />
          <Stack.Screen name="HappeningNow" component={HappeningNowScreen} />
          <Stack.Screen name="Reviews" component={ReviewScreen} />
          <Stack.Screen name="MemoryWall" component={MemoryWallScreen} />
          <Stack.Screen name="GroupTickets" component={GroupTicketScreen} />
          <Stack.Screen name="Loyalty" component={LoyaltyScreen} />
          <Stack.Screen name="Badges" component={BadgesScreen} />
          <Stack.Screen name="MoodSearch" component={MoodSearchScreen} />
          <Stack.Screen name="Curators" component={CuratorListsScreen} />
          <Stack.Screen name="Community" component={CommunityBoardScreen} />
          <Stack.Screen name="Itineraries" component={ItineraryPlannerScreen} />
        </>
      )}
    </Stack.Navigator>
  );
}

export default function App() {
  const [fontsLoaded, setFontsLoaded] = useState(false);

  useEffect(() => {
    Font.loadAsync({ ...Ionicons.font })
      .then(() => setFontsLoaded(true))
      .catch(() => setFontsLoaded(true));
  }, []);

  if (!fontsLoaded) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#0A0A0F' }}>
        <ActivityIndicator size="large" color="#8B5CF6" />
      </View>
    );
  }

  return (
    <AuthProvider>
      <NavigationContainer linking={linking}>
        <AppNavigator />
      </NavigationContainer>
    </AuthProvider>
  );
}
