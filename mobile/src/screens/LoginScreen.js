import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  Alert,
  Dimensions,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { BlurView } from 'expo-blur';
import { Ionicons } from '@expo/vector-icons';
import Animated, {
  FadeInDown,
  FadeInUp,
  useAnimatedStyle,
  useSharedValue,
  withRepeat,
  withTiming,
  withSpring,
  withSequence,
  Easing,
} from 'react-native-reanimated';
import * as Haptics from 'expo-haptics';
import { useAuth } from '../context/AuthContext';

const { width, height } = Dimensions.get('window');

export default function LoginScreen({ navigation }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();

  // Animations
  const floatingAnim1 = useSharedValue(0);
  const floatingAnim2 = useSharedValue(0);
  const floatingAnim3 = useSharedValue(0);
  const glowAnim = useSharedValue(1);

  useEffect(() => {
    // Floating orbs animation
    floatingAnim1.value = withRepeat(
      withTiming(1, { duration: 3000, easing: Easing.inOut(Easing.ease) }),
      -1,
      true
    );
    floatingAnim2.value = withRepeat(
      withTiming(1, { duration: 4000, easing: Easing.inOut(Easing.ease) }),
      -1,
      true
    );
    floatingAnim3.value = withRepeat(
      withTiming(1, { duration: 5000, easing: Easing.inOut(Easing.ease) }),
      -1,
      true
    );
    glowAnim.value = withRepeat(
      withTiming(1.5, { duration: 2000 }),
      -1,
      true
    );
  }, []);

  const orb1Style = useAnimatedStyle(() => ({
    transform: [
      { translateY: floatingAnim1.value * 30 },
      { translateX: floatingAnim1.value * 20 },
    ],
    opacity: 0.6,
  }));

  const orb2Style = useAnimatedStyle(() => ({
    transform: [
      { translateY: floatingAnim2.value * -40 },
      { translateX: floatingAnim2.value * -30 },
    ],
    opacity: 0.5,
  }));

  const orb3Style = useAnimatedStyle(() => ({
    transform: [
      { translateY: floatingAnim3.value * 25 },
      { translateX: floatingAnim3.value * -15 },
    ],
    opacity: 0.4,
  }));

  const glowStyle = useAnimatedStyle(() => ({
    opacity: glowAnim.value * 0.3,
  }));

  const handleLogin = async () => {
    if (!email || !password) {
      Haptics.notificationAsync(Haptics.NotificationFeedbackType.Error);
      Alert.alert('Error', 'Please fill in all fields');
      return;
    }

    setIsLoading(true);
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);

    const result = await login(email, password);
    setIsLoading(false);

    if (!result.success) {
      Haptics.notificationAsync(Haptics.NotificationFeedbackType.Error);
      Alert.alert('Error', result.error);
    } else {
      Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
    }
  };

  return (
    <View style={styles.container}>
      {/* Animated Background */}
      <LinearGradient
        colors={['#0A0A0F', '#1A0B2E', '#2D1B4E', '#1A0B2E', '#0A0A0F']}
        style={StyleSheet.absoluteFill}
      />

      {/* Floating Orbs */}
      <Animated.View style={[styles.orb, styles.orb1, orb1Style]}>
        <LinearGradient
          colors={['#8B5CF6', '#6D28D9']}
          style={styles.orbGradient}
        />
      </Animated.View>
      <Animated.View style={[styles.orb, styles.orb2, orb2Style]}>
        <LinearGradient
          colors={['#EC4899', '#BE185D']}
          style={styles.orbGradient}
        />
      </Animated.View>
      <Animated.View style={[styles.orb, styles.orb3, orb3Style]}>
        <LinearGradient
          colors={['#3B82F6', '#1D4ED8']}
          style={styles.orbGradient}
        />
      </Animated.View>

      {/* Glow Effect */}
      <Animated.View style={[styles.glow, glowStyle]}>
        <LinearGradient
          colors={['#8B5CF6', 'transparent']}
          style={styles.glowGradient}
        />
      </Animated.View>

      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.content}
      >
        {/* Logo Section */}
        <Animated.View
          entering={FadeInDown.delay(200).springify()}
          style={styles.logoSection}
        >
          <View style={styles.logoContainer}>
            <LinearGradient
              colors={['#8B5CF6', '#EC4899']}
              style={styles.logoGradient}
            >
              <Ionicons name="calendar" size={40} color="#FFFFFF" />
            </LinearGradient>
          </View>
          <Text style={styles.logoText}>TIRANA</Text>
          <Text style={styles.logoSubtext}>EVENTS</Text>
          <Text style={styles.tagline}>Discover amazing events around you</Text>
        </Animated.View>

        {/* Form Section */}
        <Animated.View
          entering={FadeInUp.delay(400).springify()}
          style={styles.formSection}
        >
          <BlurView intensity={20} tint="dark" style={styles.formBlur}>
            <LinearGradient
              colors={['rgba(139,92,246,0.1)', 'rgba(109,40,217,0.05)']}
              style={styles.formContainer}
            >
              {/* Email Input */}
              <View style={styles.inputWrapper}>
                <View style={styles.inputIconContainer}>
                  <Ionicons name="mail-outline" size={20} color="#8B5CF6" />
                </View>
                <TextInput
                  style={styles.input}
                  placeholder="Email address"
                  placeholderTextColor="#6B7280"
                  value={email}
                  onChangeText={setEmail}
                  autoCapitalize="none"
                  keyboardType="email-address"
                />
              </View>

              {/* Password Input */}
              <View style={styles.inputWrapper}>
                <View style={styles.inputIconContainer}>
                  <Ionicons name="lock-closed-outline" size={20} color="#8B5CF6" />
                </View>
                <TextInput
                  style={styles.input}
                  placeholder="Password"
                  placeholderTextColor="#6B7280"
                  value={password}
                  onChangeText={setPassword}
                  secureTextEntry={!showPassword}
                />
                <TouchableOpacity
                  style={styles.eyeButton}
                  onPress={() => {
                    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
                    setShowPassword(!showPassword);
                  }}
                >
                  <Ionicons
                    name={showPassword ? 'eye-outline' : 'eye-off-outline'}
                    size={20}
                    color="#6B7280"
                  />
                </TouchableOpacity>
              </View>

              {/* Forgot Password */}
              <TouchableOpacity style={styles.forgotButton}>
                <Text style={styles.forgotText}>Forgot password?</Text>
              </TouchableOpacity>

              {/* Login Button */}
              <TouchableOpacity
                style={styles.loginButton}
                onPress={handleLogin}
                disabled={isLoading}
                activeOpacity={0.8}
              >
                <LinearGradient
                  colors={['#8B5CF6', '#6D28D9']}
                  start={{ x: 0, y: 0 }}
                  end={{ x: 1, y: 1 }}
                  style={styles.loginGradient}
                >
                  {isLoading ? (
                    <Text style={styles.loginText}>Signing in...</Text>
                  ) : (
                    <>
                      <Text style={styles.loginText}>Sign In</Text>
                      <Ionicons name="arrow-forward" size={20} color="#FFF" />
                    </>
                  )}
                </LinearGradient>
              </TouchableOpacity>

              {/* Divider */}
              <View style={styles.divider}>
                <View style={styles.dividerLine} />
                <Text style={styles.dividerText}>or continue with</Text>
                <View style={styles.dividerLine} />
              </View>

              {/* Social Buttons */}
              <View style={styles.socialButtons}>
                <TouchableOpacity style={styles.socialButton}>
                  <BlurView intensity={20} tint="dark" style={styles.socialBlur}>
                    <Ionicons name="logo-google" size={24} color="#FFF" />
                  </BlurView>
                </TouchableOpacity>
                <TouchableOpacity style={styles.socialButton}>
                  <BlurView intensity={20} tint="dark" style={styles.socialBlur}>
                    <Ionicons name="logo-apple" size={24} color="#FFF" />
                  </BlurView>
                </TouchableOpacity>
                <TouchableOpacity style={styles.socialButton}>
                  <BlurView intensity={20} tint="dark" style={styles.socialBlur}>
                    <Ionicons name="logo-facebook" size={24} color="#FFF" />
                  </BlurView>
                </TouchableOpacity>
              </View>
            </LinearGradient>
          </BlurView>
        </Animated.View>

        {/* Sign Up Link */}
        <Animated.View
          entering={FadeInUp.delay(600).springify()}
          style={styles.signupSection}
        >
          <Text style={styles.signupText}>Don't have an account? </Text>
          <TouchableOpacity
            onPress={() => {
              Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
              navigation.navigate('Register');
            }}
          >
            <Text style={styles.signupLink}>Sign Up</Text>
          </TouchableOpacity>
        </Animated.View>
      </KeyboardAvoidingView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  orb: {
    position: 'absolute',
    borderRadius: 1000,
  },
  orb1: {
    width: 300,
    height: 300,
    top: -100,
    right: -100,
  },
  orb2: {
    width: 250,
    height: 250,
    bottom: -50,
    left: -80,
  },
  orb3: {
    width: 200,
    height: 200,
    top: height * 0.4,
    right: -60,
  },
  orbGradient: {
    flex: 1,
    borderRadius: 1000,
    opacity: 0.3,
  },
  glow: {
    position: 'absolute',
    top: height * 0.3,
    left: width * 0.5 - 150,
    width: 300,
    height: 300,
    borderRadius: 150,
  },
  glowGradient: {
    flex: 1,
    borderRadius: 150,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    padding: 24,
  },
  logoSection: {
    alignItems: 'center',
    marginBottom: 48,
  },
  logoContainer: {
    width: 80,
    height: 80,
    borderRadius: 40,
    overflow: 'hidden',
    marginBottom: 16,
    elevation: 10,
    shadowColor: '#8B5CF6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.5,
    shadowRadius: 12,
  },
  logoGradient: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  logoText: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#FFFFFF',
    letterSpacing: 2,
  },
  logoSubtext: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#8B5CF6',
    letterSpacing: 3,
    marginTop: -8,
  },
  tagline: {
    fontSize: 14,
    color: '#9CA3AF',
    marginTop: 12,
    letterSpacing: 0.5,
  },
  formSection: {
    borderRadius: 32,
    overflow: 'hidden',
    marginBottom: 24,
  },
  formBlur: {
    borderRadius: 32,
  },
  formContainer: {
    padding: 24,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.3)',
  },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(31,31,46,0.8)',
    borderRadius: 16,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.2)',
  },
  inputIconContainer: {
    width: 48,
    height: 56,
    justifyContent: 'center',
    alignItems: 'center',
  },
  input: {
    flex: 1,
    color: '#FFFFFF',
    fontSize: 16,
    paddingVertical: 16,
  },
  eyeButton: {
    width: 48,
    height: 56,
    justifyContent: 'center',
    alignItems: 'center',
  },
  forgotButton: {
    alignSelf: 'flex-end',
    marginBottom: 24,
  },
  forgotText: {
    color: '#8B5CF6',
    fontSize: 14,
    fontWeight: '600',
  },
  loginButton: {
    borderRadius: 16,
    overflow: 'hidden',
    elevation: 8,
    shadowColor: '#8B5CF6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.4,
    shadowRadius: 12,
  },
  loginGradient: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 18,
  },
  loginText: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
    marginRight: 8,
    letterSpacing: 0.5,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 24,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: 'rgba(139,92,246,0.2)',
  },
  dividerText: {
    color: '#6B7280',
    fontSize: 12,
    marginHorizontal: 16,
  },
  socialButtons: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 16,
  },
  socialButton: {
    width: 56,
    height: 56,
    borderRadius: 28,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.3)',
  },
  socialBlur: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  signupSection: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  signupText: {
    color: '#9CA3AF',
    fontSize: 14,
  },
  signupLink: {
    color: '#8B5CF6',
    fontSize: 14,
    fontWeight: 'bold',
  },
});
