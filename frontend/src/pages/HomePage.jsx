import {useEffect} from "react";

function HomePage() {
    useEffect(() => {
        document.title = "Home | Ecommerce Platform";
    }, []);

    return (
        <main className="container page-container">
            <div className="neon-card text-center">
                <div className="retro-grid"></div>

                <div className="position-relative">
                    <p className="retro-label">ECOMMERCE PLATFORM</p>

                    <h1 className="neon-title">
                        Welcome
                    </h1>

                    <p className="neon-text">
                        Welcome to the ecommerce platform.
                    </p>
                </div>
            </div>
        </main>
    );
}

export default HomePage;