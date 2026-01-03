import { MongoClient, Db } from 'mongodb';
import { Service } from 'typedi';

@Service()
export class Database {
	private static db: Db;

	async connectDB(): Promise<Db> {
		console.log('\x1b[92m[mongo] Connecting to Database...\x1b[0m');

		const uri = process.env.MONGO_URI || 'mongodb://localhost:27017';
		const dbName = 'flashcards';
		const client = new MongoClient(uri);
		await client.connect();

		Database.db = client.db(dbName);

		console.log('\x1b[92m[mongo] Connected!\x1b[0m');
		return Database.db;
	}

	getDB(): Db {
		return Database.db;
	}
}
