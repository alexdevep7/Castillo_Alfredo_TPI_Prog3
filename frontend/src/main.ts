import "./style.css";
import { initData } from "./utils/localStorage";
import { protectRoute } from "./utils/navigate";

const initApp = async () => {
  await initData();
  protectRoute();
};

initApp();
