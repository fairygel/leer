import { Service } from 'typedi';
import { JWT } from './JWT';
import { NextFunction, Request, Response } from 'express';

@Service()
export class AuthMiddleware {
	constructor(
		private jwt: JWT
	) {
		this.authenticate = this.authenticate.bind(this);
	}

	authenticate(req: Request, res: Response, next: NextFunction) {
		const authHeader = req.headers.authorization;

		if (!authHeader) {
			res.status(401).json({ error: 'No Auth Token' });
			return;
		}

		const token = authHeader.split(' ')[1];

		if (!token) {
			res.status(401).json({ error: 'Invalid Token Format' });
			return;
		}

		try {
			req.user = this.jwt.verifyToken(token) as { userId: string; username: string };
			next();
		} catch (error) {
			res.status(401).json({ error: 'Invalid or Expired Token' });
		}
	}
}