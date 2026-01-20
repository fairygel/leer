import { fetcher } from './fetcher';

export const AuthService = {
	async register(userData: any) {
		const response = await fetcher('/auth/register', 'POST', {
			body: JSON.stringify(userData),
		});

		if (!response.ok) {
			throw new Error('Registration failed');
		}

		return await response.json();
	},

	async login(credentials: any) {
		const response = await fetcher('/auth/login', 'POST', {
			body: JSON.stringify(credentials),
		});

		if (!response.ok) {
			throw new Error('Login failed');
		}

		return await response.json();
	},
};

