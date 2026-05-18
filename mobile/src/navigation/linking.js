export const linking = {
  prefixes: ['tiranaevents://', 'https://tiranaevents.app'],
  config: {
    screens: {
      Login: 'login',
      Register: 'register',
      Onboarding: 'onboarding',
      Main: {
        screens: {
          Home: 'home',
          Explore: 'explore',
          Create: 'create',
          Tickets: 'tickets',
          Profile: 'profile',
        },
      },
      EventDetail: 'event/:eventId',
    },
  },
};
