import { storage } from './storage';
import { router } from 'expo-router';

const base_url = 'https://api.icry.uk/leer';

export const fetcher = async(
	endpoint: string,
	method: string = 'get',
	options: RequestInit = {}
): Promise<Response> => {
	const token = await storage.getToken();

	const headers: HeadersInit = {
		'Content-Type': 'application/json',
		...(token ? { 'Authorization': `Bearer ${token}` } : {}),
		...options.headers,
	};

	let useSlash = '';

	if (!endpoint.startsWith('/')) useSlash = '/';

	try {
		const response = await fetch(`${base_url}${useSlash}${endpoint}`, {
			...options,
			method,
			headers,
		});

		if (response.status === 401 && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/register')) {
			await storage.removeToken();
			router.replace('/(auth)/login');
		}

		return response;
	} catch (e) {
		console.error('api error:', e);
		throw e;
	}
};

