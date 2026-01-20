import { storage } from './storage';

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
		return await fetch(`${base_url}${useSlash}${endpoint}`, {
			...options,
			method,
			headers,
		});
	} catch (e) {
		console.error('api error:', e);
		throw e;
	}
};