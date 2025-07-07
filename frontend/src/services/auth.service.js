import api from './api'

class AuthService {
  async login(username, password) {
    const response = await api.post('/auth/signin', {
      username,
      password
    })
    
    if (response.data.accessToken) {
      const user = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email,
        roles: response.data.roles
      }
      
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('token', response.data.accessToken)
      
      return { user, token: response.data.accessToken }
    }
    
    return response.data
  }

  async register(userData) {
    const response = await api.post('/auth/signup', {
      username: userData.username,
      email: userData.email,
      password: userData.password,
      roles: userData.roles || ['user']
    })
    
    return response.data
  }

  logout() {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'))
  }
}

export default new AuthService()
