const API_URL = 'http://localhost:8080';

export const environment = {
  production: false,
  api: {
    url: API_URL,
    auth: {
      login: API_URL + '/auth/login',
      register: API_URL + '/auth/register',
      refresh: API_URL + '/auth/refresh',
    },
  },
};
