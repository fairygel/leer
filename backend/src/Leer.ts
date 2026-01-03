import 'reflect-metadata';
import express from 'express';
import dotenv from 'dotenv';
import { Database } from './config/database';
import { SetRouter } from './routes/setRoutes';
import { Service } from 'typedi';

@Service()
export class Leer {
	constructor (
		private setRouter: SetRouter,
		private database: Database
	) {}

	private configure(): express.Express {
		dotenv.config();

		const app = express();
		app.use(express.json());

		return app
	}

	async start(PORT: number) {
		try {
			const app = this.configure();

			await this.database.connectDB();

			app.use('/api/sets', this.setRouter.createSetRoutes());

			app.listen(PORT, () => {
				console.log(
					`\x1b[36m[leer] Server is running on http://localhost:${PORT}\x1b[0m`
				);
			});
		} catch (error) {
			console.error(
				`\x1b[36m[leer] Failed to start server: ${error}\x1b[0m`
			);
		}
	}
}
